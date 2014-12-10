package studies.algorithms.api

import com.mongodb.DBObject
import com.mongodb.casbah.MongoCollection
import com.mongodb.casbah.commons.MongoDBObject
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.bson.types.ObjectId
import org.scalatra.BadRequest
import studies.algorithms.{PointCloud, Vector2d, TestPlotter}

import org.apache.commons.math3.stat.regression
import scala.math

import org.scalatra.swagger._

class AlgorithmRestApi(imagesColl: MongoCollection, userEstimatesColl: MongoCollection)(implicit val swagger: Swagger)  extends AlgorithmRestApiStack with SwaggerSupport {
  override protected val applicationName = Some("main")
  protected val applicationDescription = "API for the image comparision algorithm"

  val getImages =
    (apiOperation[List[Image]]("getImages")
      summary "Get list of images"
      notes "About the return json: if 'is_self_contained' is true, the image is contained in the app. If it is false, the 'path' value should be valid URL.")
//parameter queryParam[Option[String]]("name").description("A name to search for")
  post("/images", operation(getImages)) {
    contentType = formats("json")
    imagesColl.find().map(Image.fromDbObject).toSeq
  }

  get("/image-files/*") {
    serveStaticResource() getOrElse resourceNotFound()
  }

  get("/swagger*") {
    serveStaticResource() getOrElse resourceNotFound()
  }

  val addComparisionResult =
    (apiOperation[BasicResult]("addcompresult")
      summary "Add user estimate of correspondence of images."
      consumes "x-www-form-urlencoded"
      parameter queryParam[String]("image").description("The slug of the image.")
      parameter queryParam[Double]("square_error").description("Square error, received from `compare`.")
      parameter queryParam[Double]("user_estimate").description("User error, value between 0.1. E.g. if user thinks that image is 20% match, then this is 0.2"))

  post("/user-estimates/add", operation(addComparisionResult)) {
    contentType = formats("json")
    if (params.get("image") == None || params.get("square_error") == None || params.get("user_estimate") == None)
      halt(400, "error" -> "Some parameters missing")
    val imageId = getImageOidFromSlug(params("image"))
    userEstimatesColl.insert(AnalysisResult(imageId, params("square_error").toDouble, params("user_estimate").toDouble).toDbObject)
    BasicResult("success")
  }

  val listComparisionResults =
    (apiOperation[AnalysisResultOutput]("listcompresults")
      summary "Lists entered estimates of given image."
      parameter queryParam[String]("image").description("The slug of the image."))

  post("/user-estimates/list", operation(listComparisionResults)) {
    contentType = formats("json")
    val imageId = getImageOidFromSlug(params("image"))
    val query = MongoDBObject("image_id" -> imageId)
    val estims = userEstimatesColl.find(query).map(AnalysisResult.fromDbObject).toSeq
    estims.map(estim => AnalysisResultOutput(estim.square_error, estim.user_estimate))
  }

  val clearComparisionResult =
    (apiOperation[BasicResult]("clearcompresults")
      summary "Clears all estimates of given image."
      parameter queryParam[String]("image").description("The slug of the image."))

  post("/user-estimates/clear", operation(clearComparisionResult)) {
    contentType = formats("json")
    if (params.get("image") == None) halt(400, "error" -> "Some parameters missing")
    val imageId = getImageOidFromSlug(params("image"))
    val query = MongoDBObject("image_id" -> imageId)
    userEstimatesColl.remove(query)
    BasicResult("success")
  }

  val compare =
    (apiOperation[ComparisionResult]("compare")
      summary "Run the comparision between selected image and given points."
      notes "Square error is returned for adding new comparision results with `/comparisions-result/add`."
      parameter queryParam[String]("image").description("The slug of the image.")
      parameter queryParam[String]("points").description("The (x,y) point pairs as a space separated number list. For example \"1 50 2 60 3 10\" means points (1,50), (2,60) and (3,10)."))

  post("/compare", operation(compare)) {
    contentType = formats("json")
    if (params.get("image") == None || params.get("points") == None) halt(400, "error" -> "Some parameters missing")

    val image = getImageFromSlug(params("image"))
    val imagePath = servletContext.getResource("/image-files/" + image.file.path).toURI

    val modelCloud = TestPlotter.getCenteredCloud(imagePath)

    val drawnPixels = params("points")
      .split(" ")
      .grouped(2)
      .map(arr => Vector2d(arr(0).toDouble,arr(1).toDouble)).toList

    val drawnCloud = PointCloud(drawnPixels)

    val alignedDrawnCloud = drawnCloud.alignByStandardDeviation(modelCloud).downsample(100)
    val finalModelCloud = modelCloud.downsample(100)

    val CMAESResult = alignedDrawnCloud.runCMAES(finalModelCloud)

    val finalDrawnCloud = alignedDrawnCloud.transformByCMAESGuess(CMAESResult)
    val squareError = finalDrawnCloud.squareErrorTo(finalModelCloud)

    //Calculate approx diff constant here (e.g. with brute force in the beginning)
    val diffConstant = calculateDiffConstant(getImageOidFromSlug(params("image")))

    val systemEstimate = math.exp(-0.5 * squareError / diffConstant)

    ComparisionResult(squareError, systemEstimate, diffConstant)
  }

  def getImageFromSlug(slug: String) = {
    val query = MongoDBObject("slug" -> params("image"))
    imagesColl.findOne(query).map(Image.fromDbObject).getOrElse(
      halt(400, "error" -> "Image not found")
    )
  }

  def getImageOidFromSlug(slug: String) = {
    val query = MongoDBObject("slug" -> params("image"))
    imagesColl.findOne(query).map(_.get("_id").asInstanceOf[ObjectId]).getOrElse(
      halt(400, "error" -> "Image not found")
    )
  }

  def calculateDiffConstant(imageId: ObjectId) = {

    val query = MongoDBObject("image_id" -> imageId)
    val userEstimates = userEstimatesColl.find(query).map(AnalysisResult.fromDbObject).toSeq

    if (!userEstimates.isEmpty) {
      val singleDiffConsts = userEstimates.map(estim => -estim.square_error / (2.0 * math.log(estim.user_estimate)))
      singleDiffConsts.sum / singleDiffConsts.length
    }
    else {
      100.0
    }
  }

}
