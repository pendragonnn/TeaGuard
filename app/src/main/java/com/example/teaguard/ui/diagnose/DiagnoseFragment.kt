package com.example.teaguard.ui.diagnose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import com.example.teaguard.R
import com.example.teaguard.databinding.FragmentDiagnoseBinding

class DiagnoseFragment : Fragment() {
    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val diagnosis = arguments?.getString(ARG_DIAGNOSIS) ?: ""
        binding.tvDiagnose.text = diagnosis
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_DIAGNOSIS = "diagnosis"

        fun newInstance(diagnosis: String): DiagnoseFragment {
            val fragment = DiagnoseFragment()
            val args = Bundle()
            args.putString(ARG_DIAGNOSIS, diagnosis)
            fragment.arguments = args
            return fragment
        }
    }
}