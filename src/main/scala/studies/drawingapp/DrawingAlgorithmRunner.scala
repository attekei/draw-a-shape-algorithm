package studies.drawingapp

import java.awt.Desktop
import java.io.File

/**
 * Created by atte on 9.9.2014.
 */
object DrawingAlgorithmRunner extends App {
    val pathPrefix = "/Users/atte/Pictures/"
    val sourceImagePath = pathPrefix + "algorithm_source.png"
    val modelImagePath = pathPrefix + "algorithm_model.png"
    val resultImagePath = pathPrefix + "algorithm_result.png"

    val image = DrawingAlgorithm.buildImageWithAllTransforms(sourceImagePath, modelImagePath)
    image.write(resultImagePath)

    previewFile(resultImagePath)

    def previewFile(file: String) = Desktop.getDesktop().open(new File(file))
}
