package com.aji.melondiseasediagnose.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.aji.melondiseasediagnose.R
import com.google.android.material.card.MaterialCardView


class DashboardActivity : AppCompatActivity() {
    private lateinit var mcDiagnosa: CardView
    private lateinit var mcKonsultasi: CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        setStatusBar()
        mcDiagnosa = findViewById(R.id.mcDiagnosa)
        mcKonsultasi = findViewById(R.id.mcKonsultasi)

        mcDiagnosa.setOnClickListener {
            val intent = Intent(this, DaftarPenyakitActivity::class.java)
            startActivity(intent)
        }
        mcKonsultasi.setOnClickListener {
            val intent = Intent(this, KonsultasiActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    companion object {
        fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
            val window = activity.window
            val layoutParams = window.attributes
            if (on) {
                layoutParams.flags = layoutParams.flags or bits
            } else {
                layoutParams.flags = layoutParams.flags and bits.inv()
            }
            window.attributes = layoutParams
        }
    }
}