import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.util.Log
import com.example.teaguard.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifier(private val context: Context) {

    private val imageSize = 256
    private val classes = arrayOf("Anthracnose", "Algal Leaf", "Bird Eye Spot", "Brown Blight", "Gray Light", "Healthy", "Red Leaf Spot", "White Spot")

    private val imageProcessor = ImageProcessor.Builder()
        .add(ResizeOp(imageSize, imageSize, ResizeOp.ResizeMethod.BILINEAR))
        .build()

    fun classifyImage(image: Bitmap): String {
        val model = Model.newInstance(context)

        // Convert Bitmap to TensorImage
        val tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(image)
        val processedImage = imageProcessor.process(tensorImage)

        // Create TensorBuffer for model input
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(processedImage.buffer)

        // Run inference
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Get classification results
        val confidences = outputFeature0.floatArray
        for (i in confidences.indices) {
            Log.d("ImageClassifier", "Class $i (${classes[i]}): ${confidences[i]}")
        }
        val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: -1

        // Close the model to release resources
        model.close()

        return if (maxPos >= 0) classes[maxPos] else "Unknown"
    }
}
