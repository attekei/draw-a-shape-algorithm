package studies.algorithms

import scala.math._

//Vector in 2d space
case class Vector2d(val x: Double, val y: Double) {
  def angleTo(other: Vector2d) = {
    acos(this.dotProduct(other) / this.length / other.length)
  }

  def reflectBy(normal: Vector2d) = normal * (2 * this.dotProduct(normal)) - this
  def dotProduct(other: Vector2d) = this.x * other.x + this.y * other.y

  def *(multiplier: Double) = Vector2d(multiplier * this.x, multiplier * this.y)
  def /(divider: Double) = Vector2d(this.x / divider, this.y / divider)
  def +(other: Vector2d) = Vector2d(this.x + other.x, this.y + other.y)
  def -(other: Vector2d) = Vector2d(this.x - other.x, this.y - other.y)
  def *(other: Vector2d) = Vector2d(this.x * other.x, this.y * other.y)
  def /(other: Vector2d) = Vector2d(this.x / other.x, this.y / other.y)

  def length = sqrt(x * x + y * y)

  def normalize = Vector2d(this.x, this.y) / this.length

  override def toString = "(" + x + ", " + y + ")"

  def rotateAroundOrigin(angle: Double): Vector2d = {
    Vector2d(x * cos(angle) - y * sin(angle), x * sin(angle) + y * cos(angle))
  }

}