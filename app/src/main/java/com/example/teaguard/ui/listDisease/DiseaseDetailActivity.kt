package com.example.teaguard.ui.listDisease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.teaguard.R
import com.example.teaguard.data.remote.response.DiseaseDetailByIdResponse
import com.example.teaguard.databinding.ActivityDiseaseDetailBinding
import com.example.teaguard.foundation.utils.Result
import com.example.teaguard.ui.MainActivity
import com.example.teaguard.ui.ViewModelFactory
import kotlinx.coroutines.flow.collect
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

    private fun setUpUi(data: DiseaseDetailByIdResponse) {
        val baseUrl = "http://100.87.136.13:5000"
        val imageUrl = baseUrl + data.diseaseImgDetail
        Log.d("DiseaseDetailActivity", "bind: $imageUrl")
        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageDisease)
        binding.titleDisease.text = data.diseaseName
        binding.descDisease.text = data.diseaseExplanation
        binding.descPrevention.text = data.diseasePrevention
        binding.descRecommended.text = data.diseaseRecommendation
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            diseaseName?.let { diseaseToId[it]?.let { it1 -> viewModel.getDiseaseById(it1) } }
            viewModel.listDiseaseById.collect { result ->
                when (result) {
                    is Result.Success -> {
                        setUpUi(result.data)
                    }
                    is Result.Error -> {
                    }
                    is Result.Loading -> {

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
