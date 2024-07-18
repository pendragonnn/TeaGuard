package com.example.teaguard.ui.listDisease

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.teaguard.BuildConfig
import com.example.teaguard.R
import com.example.teaguard.data.remote.response.DiseaseDetailByIdResponse
import com.example.teaguard.databinding.ActivityDiseaseDetailBinding
import com.example.teaguard.foundation.utils.Result
import com.example.teaguard.ui.ViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class DiseaseDetailActivity : AppCompatActivity() {
    private val viewModel: DiseaseViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDiseaseDetailBinding
    private var diseaseName: String? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiseaseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.dgsBack.setOnClickListener {
            onBackPressed()
        }
        val bundle = intent.extras
        diseaseName = bundle?.getString("diseaseName")
        observeViewModel()
    }

    private fun setUpUi(diseaseDetail: DiseaseDetailByIdResponse) {
        val baseUrl = BuildConfig.BASE_URL.removeSuffix("/")
        val imageUrl = baseUrl + diseaseDetail.data?.diseaseImgDetail
        Log.d("DiseaseDetailActivity", "bind: $imageUrl")
        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageDisease)
        binding.titleDisease.text = diseaseDetail.data?.diseaseName
        binding.descDisease.text = diseaseDetail.data?.diseaseExplanation
        binding.descPrevention.text = diseaseDetail.data?.diseasePrevention
        binding.descRecommended.text = diseaseDetail.data?.diseaseRecommendation
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            diseaseName?.let { diseaseToId[it]?.let { it1 -> viewModel.getDiseaseById(it1) } }
            viewModel.listDiseaseById.collect { result ->
                when (result) {
                    is Result.Success -> {
                        binding.progressResult.visibility = android.view.View.GONE
                        setUpUi(result.data)
                    }
                    is Result.Error -> {
                        binding.progressResult.visibility = android.view.View.GONE
                        Snackbar.make(
                            binding.root,
                            "Error loading data: ${result.error}",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    is Result.Loading -> {
                        binding.progressResult.visibility = android.view.View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
