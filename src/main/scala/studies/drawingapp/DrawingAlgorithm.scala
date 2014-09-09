package studies.drawingapp

import java.io.{File, FileInputStream}

import com.sksamuel.scrimage.Image
import studies.drawingapp.transforms.{RemoveEmptyArea, Transform, CenterByGravity}

object DrawingAlgorithm {
  val allTransformsInOrder = List(
    CenterByGravity
  )

  def buildImageWithAllTransforms(imageSource: String, modelSource: String): Image = {
    val image = imageFromSource(imageSource)
    val model = imageFromSource(modelSource)
    allTransformsInOrder.foldLeft(image)((image, transform) => transform(image, model))
  }
  
  def buildImageWithTransform(imageSource: String, modelSource: String, transform: Transform): Image = {
    val image = imageFromSource(imageSource)
    val model = imageFromSource(imageSource)
    transform(image, model)
  }
  
  private def imageFromSource(imageSource: String) = {
    Image(new FileInputStream(new File(imageSource)))
  }
}