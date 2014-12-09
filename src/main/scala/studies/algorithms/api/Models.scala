package studies.algorithms.api

/**
 * Created by atte on 9.12.14.
 */

case class Pixel(x: Int, y: Int)
case class Image(slug: String, name: String, pixels: Seq[Pixel])
case class Statistics(system_params: Seq[Int], system_estimate: Int, user_estimate: Int, ds_user_points: Seq[Pixel])