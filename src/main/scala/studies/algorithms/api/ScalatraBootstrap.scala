package studies.algorithms.api

import javax.servlet.ServletContext

import com.mongodb.casbah.{MongoClientURI, MongoClient}
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  implicit val swagger = new AlgorithmRestApiSwagger

  override def init(context: ServletContext) {

    // stupid temporary test
    val mongoLabUri = Option(System.getenv("MONGOLAB_URI"))
    println("Uri:", mongoLabUri)
    val mongoClient =  if (mongoLabUri.isDefined) MongoClient(MongoClientURI(mongoLabUri.get)) else MongoClient()
    val databaseName = if (mongoLabUri.isDefined) "heroku_app32301077" else "drawing-app-algorithm"
    val imagesColl = mongoClient(databaseName)("images")
    val userEstimatesColl = mongoClient(databaseName)("user-estimates")


    context.mount(new AlgorithmRestApi(imagesColl, userEstimatesColl), "/*")
    context.mount(new ResourcesApp, "/api-docs/*")
  }
}
