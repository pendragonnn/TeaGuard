package com.example.teaguard.foundation.classification

import android.content.Context
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import com.example.teaguard.ml.Model1
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifier(private val context: Context) {

    private val imageSize = 256
    private val classes = arrayOf("Anthracnose", "Algal Leaf", "Bird Eye Spot", "Brown Blight", "Gray Light", "Healthy", "Red Leaf Spot", "White Spot")

    fun classifyImage(image: Bitmap): String {
        val model = Model1.newInstance(context)

        val resizedImage = resizeImage(image)

        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, imageSize, imageSize, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidences = outputFeature0.floatArray

        val maxPos = confidences.indices.maxByOrNull { confidences[it] } ?: -1

        model.close()

        return if (maxPos >= 0) classes[maxPos] else "Unknown"
    }

    private fun resizeImage(image: Bitmap): Bitmap {
        val dimension = Math.min(image.width, image.height)
        val thumbnail = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
        return Bitmap.createScaledBitmap(thumbnail, imageSize, imageSize, false)
    }

    private fun convertBitmapToByteBuffer(image: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0

        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255))
                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255))
            }
        }

        return byteBuffer
    }
}