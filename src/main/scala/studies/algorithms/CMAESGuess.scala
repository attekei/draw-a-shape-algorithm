package studies.algorithms

case class CMAESGuess(translation: Vector2d, scale: Vector2d, rotation: Double) {
  def toDoubleArray = Array(translation.x, translation.y, scale.x, scale.y, rotation)

}

object CMAESGuess {
  val initialGuess = CMAESGuess(Vector2d(0, 0), Vector2d(1, 1), 0)
  val lowerBounds  = CMAESGuess(Vector2d(-500, -500), Vector2d(0.01, 0.01), 0)
  val upperBounds  = CMAESGuess(Vector2d(500, 500), Vector2d(100, 100), 3.14159)

  // Initial coordinate-wise standard deviations for sampling new search points around the initial guess.
  // It is suggested to set them to the estimated distance from the initial to the desired optimum.
  val sigma = CMAESGuess(Vector2d(40, 40), Vector2d(0.1, 0.1), 0.1)

  def fromDoubleArray(array: Array[Double]) = {
    CMAESGuess(Vector2d(array(0), array(1)), Vector2d(array(2), array(3)), array(4))
  }
}