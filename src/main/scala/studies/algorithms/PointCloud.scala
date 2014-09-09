package studies.algorithms
import breeze.linalg._
import breeze.numerics._
import breeze.stats.mean



class PointCloud(private val points: List[DenseVector[Double]]) {
  def getPoints = points.map(point => (point(0).toInt, point(1).toInt))

  def gravityCenter = points.reduce(_ + _) :*= (1.0 / points.length)

  def centerToOrigoByGravity: PointCloud = {
    new PointCloud(points.map(_ - gravityCenter))
  }

  def calculateSquareError(other: PointCloud): Double = {
    def vectorPowerToTwo(v: DenseVector[Double]) = v(0) * v(0) + v(1) * v(1)

    def minDeltaToAnyGroupPoint(point: DenseVector[Double], group: PointCloud): Double = {
      // TODO check from Perttu how this is intended to be implemented
      group.points.map(otherPoint => vectorPowerToTwo(point - otherPoint)).min
    }

    val minDeltas = this.points.map(p => minDeltaToAnyGroupPoint(p, other))
    minDeltas.sum / points.length
  }

  def downsample(samples: Int = 100): PointCloud = {
    new PointCloud(util.Random.shuffle(points).take(samples))
  }

  def width = lengthAt(0)
  def height = lengthAt(1)

  def lengthAt(index: Int): Double = {
    val values = points.map(_(index))
    values.max - values.min
  }

  def toImagePixelArray(pointColor: Int, emptyColor: Int): Array[Int] = {
    // FIXME redesign the implementation
    val pixels = Array.fill((width * height).toInt)(emptyColor)

    for (point <- points) {
      val position: Int = (width * point(1) + point(0)).toInt
      pixels(position) = pointColor
    }

    pixels
  }
}

object PointCloud {
  def fromImagePixelArray(pixels: Array[Int], imageWidth: Int, pointColor: Int): PointCloud = {
    new PointCloud(
      pixels
        .zipWithIndex
        .filter { case (value, _) => value == pointColor }
        .map { case (_, index) => DenseVector(index.toDouble % imageWidth, index.toDouble / imageWidth) }
        .toList
    )
  }
}