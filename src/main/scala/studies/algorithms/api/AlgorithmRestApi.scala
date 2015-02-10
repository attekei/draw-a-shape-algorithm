package studies.algorithms.api

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import javax.imageio.ImageIO

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
      notes "If 'is_self_contained' is true, you can find the images from /image-files folder, and the `path` contains only the name of image. Otherwise the image is hosted somewhere else, and the path contains the full URL of image.")
//parameter queryParam[Option[String]]("name").description("A name to search for")
  get("/images", operation(getImages)) {
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
      notes "You probably want to run this after you have made the comparision."
      consumes "x-www-form-urlencoded"
      parameter queryParam[String]("image").description("The slug of the image.")
      parameter queryParam[Double]("square_error").description("Square error")
      parameter queryParam[Double]("user_estimate").description("User estimate. Value between 0 and 1. Example: if user thinks that image is 20% match, then value is 0.2."))

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
      notes "This runs the comparision algorithm. It uses user estimates for the calculation of correspondence. In result, the correspondence is in `systemEstimate`. The transformations array is in format [xTranslation, yTranslation, scale, rotation]."
      parameter queryParam[String]("image").description("The slug of the image.")
      parameter queryParam[String]("points").description("The (x,y) point pairs as a space separated number list. For example \"1 50 2 60 3 10\" means points (1,50), (2,60) and (3,10)."))

  post("/compare", operation(compare)) {
    contentType = formats("json")
    if (params.get("image") == None || params.get("points") == None) halt(400, "error" -> "Some parameters missing")

    val image = getImageFromSlug(params("image"))
    val imagePath = servletContext.getResource("/image-files/" + image.file.path).toURI

    val nonCenteredModelCloud = getCloudFromPath(imagePath)
    val modelCloud = nonCenteredModelCloud.centerByMean

    val drawnPixels = params("points")
      .split(" ")
      .grouped(2)
      .map(arr => Vector2d(arr(0).toDouble,arr(1).toDouble)).toList

    val nonCenteredDrawnCloud = PointCloud(drawnPixels)
    val drawnCloud = nonCenteredDrawnCloud.centerByMean

    val alignedDrawnCloud = drawnCloud.scaleByStandardDeviation(modelCloud).downsample(100)
    val finalModelCloud = modelCloud.downsample(100)

    val CMAESResult = alignedDrawnCloud.runCMAES(finalModelCloud)

    val finalDrawnCloud = alignedDrawnCloud.transformByCMAESGuess(CMAESResult)
    val squareError = finalDrawnCloud.squareErrorTo(finalModelCloud)

    //Calculate approx diff constant here (e.g. with brute force in the beginning)
    val diffConstant = calculateDiffConstant(getImageOidFromSlug(params("image")))

    val systemEstimate = math.exp(-0.5 * squareError / diffConstant)

    ComparisionResult(
      square_error = squareError,
      system_estimate = systemEstimate,
      used_diff_constant = diffConstant,
      drawing_mean = nonCenteredDrawnCloud.mean.toArray,
      model_mean = nonCenteredModelCloud.mean.toArray,
      drawing_std_dev_scale = drawnCloud.standardDeviationScale(modelCloud).toArray,
      cmaes_transformations = CMAESResult.toDoubleArray)
  }

  def getCloudFromPath(imagePath: URI) = {
    val black = Color.BLACK.getRGB

    val image = ImageIO.read(new File(imagePath))
    val imagePixels = getImagePixels(image).toArray
    PointCloud.fromImagePixelArray(imagePixels, image.getWidth, black)
  }

  def getImagePixels(img: BufferedImage) = {
    for {
      y <- 0 until img.getHeight()
      x <- 0 until img.getWidth()
    } yield img.getRGB(x, y)
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
