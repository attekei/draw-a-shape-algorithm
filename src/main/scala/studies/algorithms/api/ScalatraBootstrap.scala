package studies.algorithms.api

import javax.servlet.ServletContext

import com.mongodb.casbah.MongoClient
import org.scalatra._

class ScalatraBootstrap extends LifeCycle {
  override def init(context: ServletContext) {
    val mongoClient =  MongoClient()
    val imagesColl = mongoClient("drawing_app_algorithm")("images")
    val statisticsColl = mongoClient("drawing_app_algorithm")("images")

    context.mount(new AlgorithmRestApi(imagesColl, statisticsColl), "/*")
  }
}
