package studies.algorithms

import scala.math.{acos, sqrt}

//Vector in 2d space
class Vector2d(val x: Double, val y: Double) {
  //Reflects vector by given vector
  def reflectBy(normal: Vector2d) = {
    normal * (2 * this.dotProduct(normal)) - this
  }

  def dotProduct(other: Vector2d) = {
    this.x * other.x + this.y * other.y
  }

  def angleTo(other: Vector2d) = {
    acos(this.dotProduct(other) / this.length / other.length)
  }

  def *(multiplier: Double) = {
    new Vector2d(multiplier * this.x, multiplier * this.y)
  }

  def /(divider: Double) = {
    new Vector2d(this.x / divider, this.y / divider)
  }

  def +(other: Vector2d) = {
    new Vector2d(this.x + other.x, this.y + other.y)
  }

  def -(other: Vector2d) = {
    new Vector2d(this.x - other.x, this.y - other.y)
  }

  def length = sqrt(x * x + y * y)

  def normalize = {
    new Vector2d(this.x, this.y) / this.length
  }

  override def toString = {
    "(" + x + ", " + y + ")"
  }

}