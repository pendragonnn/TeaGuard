package com.example.teaguard.ui.listDisease

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teaguard.R
import com.example.teaguard.data.remote.response.DiseaseDetailResponseItem
import com.example.teaguard.databinding.ActivityDiseaseBinding
import com.example.teaguard.foundation.adapter.DiseaseDetailAdapter
import com.example.teaguard.ui.ViewModelFactory
import com.example.teaguard.foundation.utils.Result
import com.example.teaguard.ui.MainActivity
import kotlinx.coroutines.launch

class DiseaseActivity : AppCompatActivity() {
    private val viewModel: DiseaseViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val diseaseAdapter = DiseaseDetailAdapter()
    private lateinit var binding: ActivityDiseaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDiseaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.dgsBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        setUpView()
        observerViewModel()
    }

    private fun setUpView() {
        binding.rvDisease.layoutManager = LinearLayoutManager(this)
        binding.rvDisease.adapter = diseaseAdapter

        diseaseAdapter.setOnItemClickCallback(object : DiseaseDetailAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DiseaseDetailResponseItem) {
                navigateToDiseaseDetail(data)
            }
        })
    }

    private fun navigateToDiseaseDetail(data: DiseaseDetailResponseItem) {
        val bundle = Bundle().apply {
            putString("diseaseName", data.diseaseName)
        }
        val intent = Intent(this, DiseaseDetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun observerViewModel() {
        lifecycleScope.launch {
            viewModel.getAllDisease()
            viewModel.listDisease.collect { result ->
                when (result) {
                    is Result.Success -> {
                        diseaseAdapter.submitList(result.data)
                    }
                    is Result.Error -> {
                        // handle error
                    }
                    is Result.Loading -> {
                        // show loading
                    }
                }
            }
        }
    }
}
