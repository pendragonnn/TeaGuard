package com.example.teaguard.ui.home

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.teaguard.R
import com.example.teaguard.databinding.FragmentHomeBinding
import com.example.teaguard.ml.Model1
import com.example.teaguard.ui.ViewModelFactory
import com.example.teaguard.ui.diagnose.DiagnoseDetailActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val imageSize = 256
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private val viewModel : HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnHsCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            } else {
                requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 100)
            }
        }


        binding.btnHsGallery.setOnClickListener {
            val cameraIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        }
        binding.cdHomeScreenAnalyze.setOnClickListener {
            val intent = Intent(activity, DiagnoseDetailActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 3) {
                var image = data?.extras?.get("data") as? Bitmap
                if (image != null) {
                    val dimension = Math.min(image.width, image.height)
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                    // Ubah tampilan gambar di sini

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                    //classifyImage(image)
                }
            } else if (requestCode == 1) {
                val dat = data?.data
                var image: Bitmap? = null
                try {
                    if (dat != null) {
                        image =
                            MediaStore.Images.Media.getBitmap(Application().contentResolver, dat)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                if (image != null) {
                    // Ubah tampilan gambar di sini

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                    //classifyImage(image)
                }
            }
        }
    }

    fun classifyImage(image: Bitmap) {
        val model = context?.let { Model1.newInstance(it) }

        // Creates inputs for reference.
        val inputFeature0 =
            TensorBuffer.createFixedSize(intArrayOf(1, 256, 256, 3), DataType.FLOAT32)
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

        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model?.process(inputFeature0)
        val outputFeature0 = outputs?.outputFeature0AsTensorBuffer

        val confidences = outputFeature0?.floatArray ?: floatArrayOf()

        var maxPos = 0
        var maxConfidence = 0f

        for(i in confidences.indices) {
            if(confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }

        val classes = arrayOf("Anthracnose", "Algal Leaf", "Bird Eye Spot", "Brown Blight", "Gray Light", "Healthy", "Red Leaf Spot", "White Spot")

        // teks hasil
        // result.text(classes[maxPost])


        // Releases model resources if no longer used.
        if (model != null) {
            model.close()
        }
    }

}