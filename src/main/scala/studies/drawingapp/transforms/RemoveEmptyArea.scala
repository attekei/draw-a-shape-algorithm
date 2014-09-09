package studies.drawingapp.transforms

import java.awt.Color

import com.sksamuel.scrimage.Image

object RemoveEmptyArea extends Transform {
  def apply(image: Image, model: Image): Image = {
    image.autocrop(Color.white)
  }
}
