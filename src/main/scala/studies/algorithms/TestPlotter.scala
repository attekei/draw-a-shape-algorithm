package studies.algorithms

import java.awt.{Color, Desktop}
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import com.xeiam.xchart.{SwingWrapper, Chart}
import com.xeiam.xchart.StyleManager.{LegendPosition, ChartType}

object TestPlotter extends App {
  val pathPrefix = "/Users/atte/Pictures/"
  val drawnImagePath = pathPrefix + "algorithm_source_2.png"
  val modelImagePath = pathPrefix + "algorithm_model.png"

  val drawnCloud = getCenteredCloud(drawnImagePath)
  val modelCloud = getCenteredCloud(modelImagePath)

  val alignedDrawnCloud = drawnCloud.alignByStandardDeviation(modelCloud)

  val dsAlignedDrawnCloud = alignedDrawnCloud.downsample(100)
  val dsModelCloud = modelCloud.downsample(100)

  val CMAESResult =  dsAlignedDrawnCloud.runCMAES(dsModelCloud)
  val CMAESAlignedCloud = alignedDrawnCloud.transformByCMAESGuess(CMAESResult)
  val dsCMAESAlignedCloud = dsAlignedDrawnCloud.transformByCMAESGuess(CMAESResult)

  println("Result of CMAES: " + CMAESResult)
  println("Square error before CMAES: " + dsAlignedDrawnCloud.squareErrorTo(dsModelCloud))
  println("Square error after CMAES: " + dsAlignedDrawnCloud.transformByCMAESGuess(CMAESResult).squareErrorTo(dsModelCloud))

  drawScatterChart(Map(
    "CMAES Aligned cloud (downsampled)" -> dsCMAESAlignedCloud.points,
    "Model cloud (downsampled)"   -> dsModelCloud.points
  ))

  drawScatterChart(Map(
    "CMAES Aligned cloud" -> CMAESAlignedCloud.points,
    "Model cloud"   -> modelCloud.points
  ))

  def getCenteredCloud(imagePath: String) = {
    val black = Color.BLACK.getRGB

    val image = ImageIO.read(new File(imagePath))
    val imagePixels = getImagePixels(image).toArray
    val cloud = PointCloud.fromImagePixelArray(imagePixels, image.getWidth, black)
    cloud.centerByMean
  }

  def drawScatterChart(pointSeries: Map[String, List[Vector2d]]) = {
    val chart = new Chart(800, 600)
    chart.getStyleManager().setChartType(ChartType.Scatter)
    chart.getStyleManager().setChartTitleVisible(false)
    chart.getStyleManager().setLegendPosition(LegendPosition.InsideSW)
    // TODO add after XChart update: chart.getStyleManager().setMarkerSize(3)

    pointSeries.map { case (name: String, points: List[Vector2d]) =>
      chart.addSeries(name, points.map(_.x).toArray, points.map(-_.y).toArray)
    }

    new SwingWrapper(chart).displayChart()
  }

  def getImagePixels(img: BufferedImage) = {
    for {
      y <- 0 until img.getHeight()
      x <- 0 until img.getWidth()
    } yield img.getRGB(x, y)
  }

  def previewFile(file: String) = Desktop.getDesktop().open(new File(file))
}
