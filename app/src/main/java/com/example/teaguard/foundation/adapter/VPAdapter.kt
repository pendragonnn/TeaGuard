package com.example.teaguard.foundation.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.teaguard.data.local.entity.HistoryDiagnose
import com.example.teaguard.ui.diagnose.DiagnoseFragment
import com.example.teaguard.ui.diagnose.RecommendationFragment

class VPAdapter(
    fa: FragmentActivity,
    private val historyDiagnose: HistoryDiagnose
): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> DiagnoseFragment.newInstance(historyDiagnose.diagnosis)
            1 -> RecommendationFragment.newInstance(historyDiagnose.recommendation)
            else -> DiagnoseFragment()
        }
    }

}