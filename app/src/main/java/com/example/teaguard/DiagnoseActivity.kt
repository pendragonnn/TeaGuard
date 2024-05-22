package com.example.teaguard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class DiagnoseActivity : AppCompatActivity() {
    private lateinit var imgActivityBack: ImageView

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

        val mViewPager: ViewPager2 = findViewById(R.id.view_pager)
        val mTabLayout: TabLayout = findViewById(R.id.tab_layout)

        mViewPager.adapter = VPAdapter(this)

        TabLayoutMediator(mTabLayout, mViewPager) {
            tab, position ->
            when(position) {
                0 -> tab.text = "Diagnosa"
                1 -> tab.text = "Rekomendasi"
            }
        }.attach()

        imgActivityBack = findViewById(R.id.dgs_back)
        imgActivityBack.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}