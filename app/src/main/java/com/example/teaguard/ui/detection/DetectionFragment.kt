package com.example.teaguard.ui.detection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teaguard.R
import com.example.teaguard.databinding.FragmentDetectionBinding
import com.example.teaguard.foundation.adapter.HistoryDiagnoseAdapter
import com.example.teaguard.ui.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetectionFragment : Fragment(R.layout.fragment_detection) {

    private var _binding: FragmentDetectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetectionViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private val detectionAdapter = HistoryDiagnoseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeDetectionList()
    }

    private fun setupRecyclerView() {
        binding.rvResultDetection.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResultDetection.adapter = detectionAdapter
    }

    private fun observeDetectionList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.detectionList.collectLatest { historyDiagnoses ->
                Log.d("DetectionFragment", "Observing detection list. Count: ${historyDiagnoses.size}")
                detectionAdapter.submitList(historyDiagnoses)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
