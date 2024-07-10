package com.example.teaguard.ui.home

import ImageClassifier
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.example.teaguard.foundation.utils.Result
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.teaguard.R
import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.databinding.FragmentHomeBinding
import com.example.teaguard.foundation.utils.saveImageToLocalStorage
import com.example.teaguard.ui.ViewModelFactory
import com.example.teaguard.ui.diagnose.DiagnoseDetailActivity
import com.example.teaguard.ui.listDisease.DiseaseActivity
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val imageSize = 256
    private val binding get() = _binding!!
    private var lastDiagnosis: HistoryDiagnose? = null
    private val viewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }
    private val diseaseToId = mapOf(
        "Algal Leaf" to "D-001",
        "Anthracnose" to "D-002",
        "Bird Eye Spot" to "D-003",
        "Brown Blight" to "D-004",
        "Gray Light" to "D-005",
        "Red Leaf Spot" to "D-006",
        "White Spot" to "D-007",
        "Healthy" to "D-008"
    )

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val image = result.data?.extras?.get("data") as? Bitmap
                image?.let {
                    val imageUri = saveImageToLocalStorage(requireContext(), it)
                    startCrop(imageUri)
                }
            } else {
                showSnackbarError("Permission denied or image capture failed")
            }
        }

        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri = result.data?.data
                selectedImageUri?.let {
                    startCrop(it)
                }
            } else {
                showSnackbarError("Permission denied or image selection failed")
            }
        }

        binding.btnHsCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(cameraIntent)
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        binding.btnHsGallery.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            galleryLauncher.launch(galleryIntent)
        }

        binding.cdHomeScreenAnalyze.setOnClickListener {
            lastDiagnosis?.let { diagnosis ->
                navigateToDiagnoseDetail(diagnosis)
            }
        }
        binding.flHomeScreenListDisease.setOnClickListener {
            val diseaseListIntent = Intent(activity, DiseaseActivity::class.java)
            startActivity(diseaseListIntent)
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
            when (requestCode) {
                UCrop.REQUEST_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    resultUri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
                        lifecycleScope.launch {
                            classifyImage(bitmap)
                        }
                    }
                }
                UCrop.RESULT_ERROR -> {
                    val cropError = UCrop.getError(data!!)
                    cropError?.printStackTrace()
                }
            }
        }
    }

    private fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "${System.currentTimeMillis()}.jpg"))
        UCrop.of(uri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(imageSize, imageSize)
            .start(requireContext(), this)
    }

    private suspend fun classifyImage(image: Bitmap) {
        val classifier = ImageClassifier(requireContext())
        val diagnosis = classifier.classifyImage(image)

        Log.d("HomeFragment", "Diagnosis: $diagnosis")
        if (diagnosis != null) {
            saveImageAndFetchData(diagnosis, image)
        }
        else {
            showSnackbarError("Gambar tidak sesuai, silahkan foto ulang")
        }
    }

    private fun showSnackbarError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
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
                        navigateToDiagnoseDetail(historyDiagnose)
                    }
                    is Result.Error -> {
                        // Handle error state
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

    private fun navigateToDiagnoseDetail(historyDiagnose: HistoryDiagnose) {
        val intent = Intent(activity, DiagnoseDetailActivity::class.java)
        intent.putExtra("HISTORY_DIAGNOSE", historyDiagnose)
        intent.putExtra("RETURN_FRAGMENT", "home")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun updateResultUi(historyDiagnose: HistoryDiagnose) {
        binding.imgResultDiagnosis.setImageURI(Uri.parse(historyDiagnose.imageUri))
        binding.titleResultDiagnosis.text = historyDiagnose.name
        val date = historyDiagnose.date.replace("-", " ")
        binding.dateResultDiagnosis.text = date
        binding.cdHomeScreenAnalyze.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
