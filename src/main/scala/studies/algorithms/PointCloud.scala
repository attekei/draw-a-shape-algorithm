package studies.algorithms

import scala.math._

case class PointCloud(points: List[Vector2d]) {
  def centerByMean: PointCloud = {
    PointCloud(points.map(_ - mean))
  }

  lazy val mean: Vector2d = points.reduce(_ + _) / points.length

  def squareErrorTo(other: PointCloud): Double = {
    def vectorPowerToTwo(v: Vector2d) = v.x * v.x + v.y * v.y

    def minDeltaToAnyGroupPoint(point: Vector2d, group: PointCloud): Double = {
      // TODO check from Perttu how this is intended to be implemented
      group.points.map(otherPoint => vectorPowerToTwo(point - otherPoint)).min
    }

    val minDeltas = this.points.map(p => minDeltaToAnyGroupPoint(p, other))
    minDeltas.sum / points.length
  }

  def downsample(samples: Int = 100): PointCloud = {
    new PointCloud(util.Random.shuffle(points).take(samples))
  }

  def width = lengthAt(_.x)
  def height = lengthAt(_.y)

  private def lengthAt(func: Vector2d => Double): Double = {
    val values = points.map(func)
    values.max - values.min
  }

  def alignByStandardDeviation(other: PointCloud): PointCloud = {
    val scale = other.standardDeviation /: this.standardDeviation
    println("Scaling calculated by standard deviation:", scale)
    PointCloud(points.map(_ *: scale))
  }

  lazy val standardDeviation: Vector2d = {
    val devs = points.map(p => {
      Vector2d(pow(p.x - mean.x, 2), pow(p.y - mean.y, 2))
    })

    val variance = devs.reduce(_ + _) / devs.length.toDouble

    Vector2d(sqrt(variance.x), sqrt(variance.y))
  }

  def toImagePixelArray(pointColor: Int, emptyColor: Int): Array[Int] = {
    // FIXME redesign the implementation
    val pixels = Array.fill((width * height).toInt)(emptyColor)

    for (point <- points) {
      val position: Int = (width * point.y + point.x).toInt
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
        .map { case (_, index) => new Vector2d((index % imageWidth).toDouble, (index / imageWidth).toDouble) }
        .toList
    )
  }
}