package studies.algorithms.api

import org.bson.types.ObjectId

import com.novus.salat._
import com.novus.salat.global._
import com.mongodb.casbah.Imports._
import org.json4s

/**
 * Created by atte on 9.12.14.
 */
class Model[A <: AnyRef] {
  def toDbObject(implicit m: Manifest[A]) = grater[A].asDBObject(this.asInstanceOf[A])
}

class ModelCompanion[A <: AnyRef] {
  def fromDbObject(obj: DBObject)(implicit m: Manifest[A]) = grater[A].asObject(obj)
  //def fromJSON(obj: json4s.JValue)(implicit m: Manifest[A]) = grater[A].fromJSON(obj)
}

case class ImageFile(is_self_contained: Boolean, path: String)
case class Image(slug: String, name: String, file: ImageFile) extends Model[Image]
object Image extends ModelCompanion[Image]

case class Pixel(x: Int, y: Int)
case class AnalysisResult(image_id: ObjectId, square_error: Double, user_estimate: Double)
  extends Model[AnalysisResult]
object AnalysisResult extends ModelCompanion[AnalysisResult]

case class ComparisionResult(squareError: Double, systemEstimate: Double, usedDiffConstant: Double, transformations: Array[Double])
case class AnalysisResultOutput(square_error: Double, user_estimate: Double)
case class BasicResult(status: String)
