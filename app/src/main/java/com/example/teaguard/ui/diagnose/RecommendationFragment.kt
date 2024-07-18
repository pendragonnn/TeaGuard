package com.example.teaguard.ui.diagnose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.teaguard.databinding.FragmentRecommendationBinding

class RecommendationFragment : Fragment() {
    private var _binding: FragmentRecommendationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recommendation = arguments?.getString(ARG_RECOMMENDATION) ?: ""
        binding.tvRecommendation.text = recommendation
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_RECOMMENDATION = "recommendation"

        fun newInstance(recommendation: String): RecommendationFragment {
            val fragment = RecommendationFragment()
            val args = Bundle()
            args.putString(ARG_RECOMMENDATION, recommendation)
            fragment.arguments = args
            return fragment
        }
    }
}