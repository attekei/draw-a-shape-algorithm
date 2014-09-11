package studies.algorithms

import java.awt.{Color, Desktop}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import com.xeiam.xchart.{SwingWrapper, Chart}
import com.xeiam.xchart.StyleManager.{LegendPosition, ChartType}
import studies.algorithms.Vector2d

object TestPlotter extends App {
  val pathPrefix = "/Users/atte/Pictures/"
  val sourceImagePath = pathPrefix + "algorithm_source.png"
  val modelImagePath = pathPrefix + "algorithm_model.png"
  val resultImagePath = pathPrefix + "algorithm_result.png"
  val black = Color.BLACK.getRGB
  val white = Color.WHITE.getRGB

  val image = ImageIO.read(new File(sourceImagePath))
  val pixels = getImagePixels(image).toArray

  val cloud = PointCloud.fromImagePixelArray(pixels, image.getWidth, black)
  val centeredCloud = cloud.centerToOrigoByGravity.downSample(1000)

  val centeredPoints = centeredCloud.points

  // Create Chart
  val chart = new Chart(800, 600)
  chart.getStyleManager().setChartType(ChartType.Scatter)

  // Customize Chart
  chart.getStyleManager().setChartTitleVisible(false)
  chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW)

  // Series
  chart.addSeries("Gaussian Blob", centeredPoints.map(_.x).toArray, centeredPoints.map(-_.y).toArray);

  new SwingWrapper(chart).displayChart();

  def getImagePixels(img: BufferedImage) = {
    for {
      y <- 0 until img.getHeight()
      x <- 0 until img.getWidth()
    } yield img.getRGB(x, y)
  }

  def previewFile(file: String) = Desktop.getDesktop().open(new File(file))
}
