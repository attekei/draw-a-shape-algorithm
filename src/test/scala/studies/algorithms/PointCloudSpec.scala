package studies.algorithms

import java.awt.Color

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class PointCloudSpec extends Specification {

  trait cloudFromPixels extends Scope {
    def indexAt(x: Int, y: Int) = y * width + x

    val whiteColor = Color.WHITE.getRGB()
    val blackColor = Color.BLACK.getRGB()

    val width = 300
    val height = 200

    val pixels = Array.fill(width * height)(whiteColor)
    val blackPixelPositions = List((10, 170), (150, 100), (270, 50))

    blackPixelPositions.foreach(pos => {
      pixels(indexAt(pos._1, pos._2)) = blackColor
    })

    val cloud = PointCloud.fromImagePixelArray(pixels, width, blackColor)
  }

  "Point cloud creation" should {
    "produce correct points" in new cloudFromPixels {
      blackPixelPositions.toSet mustEqual cloud.getPoints.toSet
    }
  }

  "Point cloud dimension measure" should {
    "return correct width & height" in new cloudFromPixels {
      cloud.width should beCloseTo(270 - 10, 0.01)
      cloud.height should beCloseTo(170 - 50, 0.01)
    }
  }

  "Point cloud centering by gravity" should {
    "mantain old width & height" in new cloudFromPixels {
      val centeredCloud = cloud.centerToOrigoByGravity
      centeredCloud.width should beCloseTo(cloud.width, 0.01)
      centeredCloud.height should beCloseTo(cloud.height, 0.01)
    }

    "have 0,0 as gravity center" in new cloudFromPixels {
      // sanity check
      val oldCenter = cloud.gravityCenter
      oldCenter(0) should not beCloseTo(0, 0.01)
      oldCenter(1) should not beCloseTo(0, 0.01)

      val centeredCloud = cloud.centerToOrigoByGravity

      val center = centeredCloud.gravityCenter
      center(0) should beCloseTo(0, 0.01)
      center(1) should beCloseTo(0, 0.01)

      println(centeredCloud.getPoints)
    }
  }
}
