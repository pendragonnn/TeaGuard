package com.example.teaguard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    private lateinit var cardMoveActivityDiagnose: MaterialCardView
    private lateinit var boxMoveActivityMenu: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cardMoveActivityDiagnose = findViewById(R.id.cd_home_screen_analyze)
        cardMoveActivityDiagnose.setOnClickListener{
            val intent = Intent(this, DiagnoseActivity::class.java)
            startActivity(intent)
        }

        boxMoveActivityMenu = findViewById(R.id.ll_detection)
        boxMoveActivityMenu.setOnClickListener{
            val intent = Intent(this, DetectionActivity::class.java)
            startActivity(intent)
        }
    }
}