package studies.drawingapp.transforms

import com.sksamuel.scrimage.Image

/**
 * Created by atte on 9.9.2014.
 */
trait Transform {
  def apply(image: Image, model: Image): Image
}
