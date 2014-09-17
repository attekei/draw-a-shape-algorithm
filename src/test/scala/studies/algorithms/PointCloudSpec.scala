package studies.algorithms

import java.awt.Color

import org.specs2.mutable.Specification
import org.specs2.specification.Scope

class PointCloudSpec extends Specification {
  trait cloudFromPixels extends Scope {
    def indexAt(pos: Vector2d) = pos.y.toInt * width + pos.x.toInt

    val whiteColor = Color.WHITE.getRGB()
    val blackColor = Color.BLACK.getRGB()

    val width = 300
    val height = 200

    val pixels = Array.fill(width * height)(whiteColor)
    val blackPixelPositions = List(Vector2d(10, 170), Vector2d(150, 100), Vector2d(270, 50))

    blackPixelPositions.foreach(pos => {
      pixels(indexAt(pos)) = blackColor
    })

    val cloud = PointCloud.fromImagePixelArray(pixels, width, blackColor)
  }

  //trait cloudFromVectors(vectors: List[Vector2d]) extends

  "Point cloud creation from pixels" should {
    "produce correct points" in new cloudFromPixels {
      blackPixelPositions.toSet mustEqual cloud.points.toSet
    }
  }

  "Point cloud dimension measure" should {
    "return correct width & height" in new cloudFromPixels {
      cloud.width should beCloseTo(270 - 10, 0.01)
      cloud.height should beCloseTo(170 - 50, 0.01)
    }
  }

  "Point cloud centering by mean" should {
    "mantain old width & height" in new cloudFromPixels {
      val centeredCloud = cloud.centerByMean
      centeredCloud.width should beCloseTo(cloud.width, 0.01)
      centeredCloud.height should beCloseTo(cloud.height, 0.01)
    }

    "have 0,0 as mean" in new cloudFromPixels {
      // sanity check
      val oldCenter = cloud.mean
      oldCenter.x should not beCloseTo(0, 0.01)
      oldCenter.y should not beCloseTo(0, 0.01)

      val centeredCloud = cloud.centerByMean

      val center = centeredCloud.mean
      center.x should beCloseTo(0, 0.01)
      center.y should beCloseTo(0, 0.01)
    }
  }

  "Point cloud downsampling" should {
    "should return cloud with correct point count" in new cloudFromPixels {
      cloud.downsample(2).points.length shouldEqual 2
    }

    "not downsample if point count is low" in new cloudFromPixels {
      cloud.downsample(100).points.toSet shouldEqual cloud.points.toSet
    }
  }

  "Standard deviation calculating" should {
    "produce expected values" in new cloudFromPixels {
      val deviation = cloud.standardDeviation
      deviation.x should beCloseTo(106.24918, 0.001)
      deviation.y should beCloseTo(49.21608, 0.001)
    }
  }

  "Square error calculation" should {
    "produce very small value if both clouds are equal" in new cloudFromPixels {
      println("Error between clouds: ", cloud.squareErrorTo(cloud))
      cloud.squareErrorTo(cloud) should beCloseTo(0.0, 0.0001)
    }

    "produce very big value if scale of clouds is very different" in new cloudFromPixels {
      val biggerCloud = PointCloud(cloud.points.map(_ * 10))
      cloud.squareErrorTo(biggerCloud) should beGreaterThan(1000000.0)
    }

    "produce small value if there is a little translation between clouds" in new cloudFromPixels {
      val translatedCloud = PointCloud(cloud.points.map(_ + Vector2d(2, 2)))
      cloud.squareErrorTo(translatedCloud) should beBetween(1.0,100.0)
    }

    "produce small value if there is a little rotation between clouds" in new cloudFromPixels {
      val rotatedCloud = PointCloud(cloud.points.map(_.rotateAroundOrigin(math.Pi / 180)))
      cloud.squareErrorTo(rotatedCloud) should beBetween(1.0,100.0)
    }
  }
}
