package com.example.teaguard.ui.home

import ImageClassifier
import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.example.teaguard.foundation.utils.Result
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.teaguard.R
import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.databinding.FragmentHomeBinding
import com.example.teaguard.foundation.utils.saveImageToLocalStorage
import com.example.teaguard.ml.Model1
import com.example.teaguard.ui.MainActivity
import com.example.teaguard.ui.ViewModelFactory
import com.example.teaguard.ui.diagnose.DiagnoseDetailActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val imageSize = 256
    private val binding get() = _binding!!
    private var currentImageUri: Uri? = null
    private var lastDiagnosis: HistoryDiagnose? = null
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    val diseaseToId = mapOf(
        "Algal Leaf" to "D-001",
        "Anthracnose" to "D-002",
        "Bird Eye Spot" to "D-003",
        "Brown Blight" to "D-004",
        "Gray Light" to "D-005",
        "Red Leaf Spot" to "D-006",
        "White Spot" to "D-007"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        binding.btnHsGallery.setOnClickListener {
            val cameraIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(cameraIntent, 1)
        }

        binding.cdHomeScreenAnalyze.setOnClickListener {
            lastDiagnosis?.let { diagnosis ->
                val intent = Intent(activity, DiagnoseDetailActivity::class.java)
                intent.putExtra("HISTORY_DIAGNOSE", diagnosis)
                intent.putExtra("RETURN_FRAGMENT", "home")
                startActivity(intent)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dataLastResult.collect { lastDiagnosis ->
                lastDiagnosis?.let {
                    this@HomeFragment.lastDiagnosis = it
                    updateResultUi(it)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            var image: Bitmap? = null
            if (requestCode == 3) {
                image = data?.extras?.get("data") as? Bitmap
            } else if (requestCode == 1) {
                val dat = data?.data
                if (dat != null) {
                    try {
                        image = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, dat)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            Log.d("HomeFragment", "Image: $image")
            image?.let {
                viewLifecycleOwner.lifecycleScope.launch {
                    classifyImage(image)
                }
            }
        }
    }

    private suspend fun classifyImage(image: Bitmap) {
        val classifier = ImageClassifier(requireContext())
        val diagnosis = classifier.classifyImage(image)

        Log.d("HomeFragment", "Diagnosis: $diagnosis")
        if (diagnosis != "Unknown") {
            saveImageAndFetchData(diagnosis, image)
        }
    }

    private suspend fun saveImageAndFetchData(diagnosis: String, image: Bitmap) {
        val imageUri = saveImageToLocalStorage(requireContext(), image)
        val formatter = SimpleDateFormat("dd-MMMM-yyyy", Locale("id", "ID"))
        val date = Date()
        val dateNow = formatter.format(date)
        val diseaseId = diseaseToId[diagnosis]
        diseaseId?.let { id ->
            viewModel.getDiseaseById(id)
            viewModel.dataDisease.collect { result ->
                when (result) {
                    is Result.Success -> {
                        val diseaseData = result.data
                        Log.d("HomeFragment", "Disease Data: $diseaseData")
                        val historyDiagnose = HistoryDiagnose(
                            name = diagnosis,
                            imageUri = imageUri.toString(),
                            diagnosis = diseaseData.data?.diseaseExplanation ?: "",
                            recommendation = diseaseData.data?.diseaseRecommendation ?: "",
                            date = dateNow
                        )

                        viewModel.saveDiagnose(historyDiagnose)
                        lastDiagnosis = historyDiagnose
                        Log.d("HomeFragment", "History Diagnose: $historyDiagnose")
                        binding.progressResult.visibility = View.GONE
                        restartFragment()
                        updateResultUi(historyDiagnose)
                    }
                    is Result.Error -> {

                    }
                    Result.Loading -> {
                        binding.progressResult.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun restartFragment() {
        val fragmentManager = parentFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.detach(this).commitNow()
        fragmentTransaction.attach(this).commitNow()
    }

    private fun updateResultUi(historyDiagnose: HistoryDiagnose) {
        binding.imgResultDiagnosis.setImageURI(Uri.parse(historyDiagnose.imageUri))
        binding.titleResultDiagnosis.text = historyDiagnose.name
        val date = historyDiagnose.date.replace("-", " ")
        binding.dateResultDiagnosis.text = date
        binding.cdHomeScreenAnalyze.visibility = View.VISIBLE
    }
}
