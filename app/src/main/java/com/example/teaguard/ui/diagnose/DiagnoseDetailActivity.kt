package com.example.teaguard.ui.diagnose

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.teaguard.ui.MainActivity
import com.example.teaguard.R
import com.example.teaguard.databinding.ActivityDiagnoseBinding
import com.example.teaguard.foundation.adapter.VPAdapter
import com.google.android.material.tabs.TabLayoutMediator

class DiagnoseDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDiagnoseBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_diagnose)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.viewPager.adapter = VPAdapter(this)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position ->
            when(position) {
                0 -> tab.text = "Diagnosa"
                1 -> tab.text = "Rekomendasi"
            }
        }.attach()

        binding.dgsBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}