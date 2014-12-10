package studies.algorithms.api

import javax.servlet.ServletContext

import com.mongodb.casbah.MongoClient
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  implicit val swagger = new AlgorithmRestApiSwagger

  override def init(context: ServletContext) {

    // stupid temporary test
    val mongoLabUri = Option(System.getenv("MONGOLAB_URI"))
    val mongoClient =  if (mongoLabUri.isDefined) MongoClient(mongoLabUri.get) else MongoClient()
    val imagesColl = mongoClient("drawing-app-algorithm")("images")
    val userEstimatesColl = mongoClient("drawing-app-algorithm")("user-estimates")


    context.mount(new AlgorithmRestApi(imagesColl, userEstimatesColl), "/*")
    context.mount(new ResourcesApp, "/api-docs/*")
  }
}
