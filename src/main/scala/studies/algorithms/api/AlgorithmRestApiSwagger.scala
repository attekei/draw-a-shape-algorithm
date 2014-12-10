package studies.algorithms.api

import org.scalatra.swagger.{ApiInfo, NativeSwaggerBase, Swagger}

import org.scalatra.ScalatraServlet


class ResourcesApp(implicit val swagger: Swagger) extends ScalatraServlet with NativeSwaggerBase

class AlgorithmRestApiSwagger extends Swagger("1.0", "1", ApiInfo("Algorithm api", "API for the image comparision algorithm","","","",""))