package studies.algorithms

import java.util.Random

import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.optim.nonlinear.scalar.{GoalType, ObjectiveFunction}
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer.{Sigma, PopulationSize}
import org.apache.commons.math3.optim._
import org.apache.commons.math3.random.RandomGeneratorFactory

import scala.math._

case class PointCloud(points: List[Vector2d]) {
  def centerByMean: PointCloud = {
    PointCloud(points.map(_ - mean))
  }

  lazy val mean: Vector2d = points.reduce(_ + _) / points.length

  def squareErrorTo(other: PointCloud): Double = {
    def vectorPowerToTwo(v: Vector2d) = v.x * v.x + v.y * v.y

    def deltaToClosestPointInCloud(point: Vector2d, cloud: PointCloud): Double = {
      cloud.points.map(otherPoint => vectorPowerToTwo(point - otherPoint)).min
    }

    val thisMinDeltas = this.points.map(p => deltaToClosestPointInCloud(p, other))
    val otherMinDeltas = other.points.map(p => deltaToClosestPointInCloud(p, this))

    (thisMinDeltas.sum + otherMinDeltas.sum) / points.length
  }

  def diffInPercentsTo(other: PointCloud, diffConstant: Int = 100): Double = {
    100 * exp(-0.5 * squareErrorTo(other) / diffConstant)
  }

  def downsample(samples: Int = 100): PointCloud = {
    def takeEveryNth(l: List[Vector2d], n: Int) = {
      for (step <- Range(start = n - 1, end = l.length, step = n))
      yield l(step)
    }

    //Do not downsample if not necessary
    val downsamplingNotNeeded = points.length <= samples
    if (downsamplingNotNeeded) return this

    val n = points.length / samples
    val reducedPoints = takeEveryNth(points, n).toList

    new PointCloud(util.Random.shuffle(reducedPoints).take(samples))
  }

  def width = lengthAt(_.x)
  def height = lengthAt(_.y)

  private def lengthAt(func: Vector2d => Double): Double = {
    val values = points.map(func)
    values.max - values.min
  }

  def scaleByStandardDeviation(other: PointCloud): PointCloud = {
    val scale = standardDeviationScale(other)
    PointCloud(points.map(_ * scale))
  }

  def standardDeviationScale(other: PointCloud): Vector2d = {
    other.standardDeviation / this.standardDeviation
  }

  lazy val standardDeviation: Vector2d = {
    val devs = points.map(p => {
      pow(p.x - mean.x, 2) + pow(p.y - mean.y, 2)
    })

    val variance = devs.reduce(_ + _) / devs.length.toDouble
    val stdDev = sqrt(variance)

    Vector2d(stdDev, stdDev)
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

  def runCMAES(model: PointCloud): CMAESGuess = {
    val optimizer = new CMAESOptimizer(
      30,      // Maximal number of iterations.
      0.00001,    // If objective function value is smaller than stopFitness stop optimization
      true,   // isActiveCMA. Chooses the covariance matrix update method.
      0,       // Number of initial iterations, where the covariance matrix remains diagonal.
      3,       // Determines how often new random objective variables are generated in case they are out of bounds.
      RandomGeneratorFactory.createRandomGenerator(new Random()), // Random generator.
      false,   // Whether statistic data is collected.
      new SimpleValueChecker(-1, 0.000001) // Convergence checker.
    )

    val objectiveFunction = new ObjectiveFunction(new MultivariateFunction {
      override def value(p1: Array[Double]): Double = {
        val guess = CMAESGuess.fromDoubleArray(p1)
        val newCloud = transformByCMAESGuess(guess)
        val squareError = newCloud.squareErrorTo(model)

        squareError
      }
    })

    val result = optimizer.optimize(
      objectiveFunction,
      new MaxEval(100000),
      new InitialGuess(CMAESGuess.initialGuess.toDoubleArray),
      new SimpleBounds(CMAESGuess.lowerBounds.toDoubleArray, CMAESGuess.upperBounds.toDoubleArray),
      new Sigma(CMAESGuess.sigma.toDoubleArray),
      new PopulationSize(200),
      GoalType.MINIMIZE
    )

    CMAESGuess.fromDoubleArray(result.getPoint)
  }

  def transformByCMAESGuess(guess: CMAESGuess): PointCloud = {
    val transformedPoints = this.points.map((point: Vector2d) => {
      (point * guess.scale).rotateAroundOrigin(guess.rotation) + guess.translation
    })

    PointCloud(transformedPoints)
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