package studies.algorithms

class PointCloud(private val points: List[Vector2d]) {
  def getPoints = points.map(point => (point.x, point.y))

  def gravityCenter = points.reduce(_ + _) * (1.0 / points.length)

  def centerToOrigoByGravity: PointCloud = {
    new PointCloud(points.map(_ - gravityCenter))
  }

  def calculateSquareError(other: PointCloud): Double = {
    def vectorPowerToTwo(v: Vector2d) = v.x * v.x + v.y * v.y

    def minDeltaToAnyGroupPoint(point: Vector2d, group: PointCloud): Double = {
      // TODO check from Perttu how this is intended to be implemented
      group.points.map(otherPoint => vectorPowerToTwo(point - otherPoint)).min
    }

    val minDeltas = this.points.map(p => minDeltaToAnyGroupPoint(p, other))
    minDeltas.sum / points.length
  }

  def downSample(samples: Int = 100): PointCloud = {
    new PointCloud(util.Random.shuffle(points).take(samples))
  }

  def width = lengthAt(_.x)
  def height = lengthAt(_.y)

  def lengthAt(func: Vector2d => Double): Double = {
    val values = points.map(func)
    values.max - values.min
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