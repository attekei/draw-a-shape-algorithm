package studies.algorithms

case class CMAESGuess(translation: Vector2d, scale: Double, rotation: Double) {
  def toDoubleArray = Array(translation.x, translation.y, scale, rotation)

  override def toString = {
    "Translation: " + translation + ", scale: " + scale + ", rotation: " + rotation
  }
}

object CMAESGuess {
  val initialGuess = CMAESGuess(Vector2d(0, 0), 1, 0)
  val lowerBounds  = CMAESGuess(Vector2d(-150, -150), 0.6, -Math.PI / 6)
  val upperBounds  = CMAESGuess(Vector2d(150, 150), 1.4, Math.PI / 6)

  // Initial coordinate-wise standard deviations for sampling new search points around the initial guess.
  // It is suggested to set them to the estimated distance from the initial to the desired optimum.
  val sigma = CMAESGuess(Vector2d(75, 75), 0.3, Math.PI / 12)

  def fromDoubleArray(array: Array[Double]) = {
    CMAESGuess(Vector2d(array(0), array(1)), array(2), array(3))
  }
}