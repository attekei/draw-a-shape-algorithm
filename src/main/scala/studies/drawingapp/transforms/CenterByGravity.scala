package studies.drawingapp.transforms
import com.sksamuel.scrimage.Image

object CenterByGravity extends Transform {
  def apply(image: Image, model: Image): Image = {
    val pixels = image.pixels.zipWithIndex

    val paintedPixels = pixels.filter { case (value, _) => pixelIsEmpty(value) }

    val paintedXPositions = paintedPixels.map { case (value, i) => i % image.width }
    val gravityCenterX = paintedXPositions.sum / paintedPixels.length

    val paintedYPositions = paintedPixels.map { case (value, i) => i / image.height }
    val gravityCenterY = paintedYPositions.sum / paintedPixels.length

    image.translate(image.width / 2 - gravityCenterX, image.height / 2 - gravityCenterY)
  }

  def pixelIsEmpty(value: Int) = value == 0xFF000000
}