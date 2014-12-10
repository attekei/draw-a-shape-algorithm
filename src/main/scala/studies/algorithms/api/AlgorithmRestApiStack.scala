package studies.algorithms.api

import org.json4s.{Formats, DefaultFormats}
import org.scalatra._
import org.scalatra.json._
import org.scalatra.swagger.SwaggerSupport

trait AlgorithmRestApiStack extends ScalatraServlet with NativeJsonSupport {
  // JSON serialization
  protected implicit val jsonFormats: Formats = DefaultFormats

  notFound {
    // remove content type in case it was set through an action
    contentType = null

    resourceNotFound()
  }
}
