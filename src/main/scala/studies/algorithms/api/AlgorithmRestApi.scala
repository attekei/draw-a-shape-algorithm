package studies.algorithms.api

import com.mongodb.casbah.MongoCollection
import org.json4s.{Formats, DefaultFormats}
import org.scalatra._
import scalate.ScalateSupport

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._

class AlgorithmRestApi(imagesColl: MongoCollection, statisticsColl: MongoCollection) extends AlgorithmRestApiStack {
  def dbObjectToImage(obj: DBObject) = grater[Image].asObject(obj)

  // JSON serialization
  protected implicit val jsonFormats: Formats = DefaultFormats
  before() { contentType = formats("json") }

  get("/") {
    val images = imagesColl.find().map(dbObjectToImage).toSeq
    images
  }

}
