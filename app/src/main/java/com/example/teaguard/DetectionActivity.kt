package com.example.teaguard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class DetectionActivity : AppCompatActivity() {
    private lateinit var imgActivityBackHome: ImageView
    private lateinit var boxActivityBackHome: LinearLayout
    private lateinit var cardActivityDiagnose: RelativeLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detection)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imgActivityBackHome = findViewById(R.id.ds_img_back)
        imgActivityBackHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        boxActivityBackHome = findViewById(R.id.ds_ll_home)
        boxActivityBackHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        cardActivityDiagnose = findViewById(R.id.ds_rl_home_screen_card_analyze)
        cardActivityDiagnose.setOnClickListener{
            val intent = Intent(this, DiagnoseActivity::class.java)
            startActivity(intent)
        }
    }
}