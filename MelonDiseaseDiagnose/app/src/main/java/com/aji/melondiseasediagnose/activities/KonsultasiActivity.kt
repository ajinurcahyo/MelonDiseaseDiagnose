package com.aji.melondiseasediagnose.activities

import android.app.Activity
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aji.melondiseasediagnose.R
import com.aji.melondiseasediagnose.adapter.KonsultasiAdapter
import com.aji.melondiseasediagnose.db.DatabaseHelper
import com.aji.melondiseasediagnose.model.ModelKonsultasi
import com.google.android.material.button.MaterialButton


class KonsultasiActivity : AppCompatActivity() {
    private var sqLiteDatabase: SQLiteDatabase? = null
    private var konsultasiAdapter: KonsultasiAdapter? = null
    private var modelKonsultasiArrayList: ArrayList<ModelKonsultasi> = ArrayList<ModelKonsultasi>()
    private var databaseHelper: DatabaseHelper? = null
    private var toolbar: Toolbar? = null
    private lateinit var rvGejalaPenyakit: RecyclerView
    private lateinit var btnHasilDiagnosa: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi)
        setStatusBar()
        databaseHelper = DatabaseHelper(this)
        if (databaseHelper!!.openDatabase()) sqLiteDatabase = databaseHelper!!.readableDatabase
        toolbar = findViewById(R.id.toolbar)
        rvGejalaPenyakit = findViewById(R.id.rvGejalaPenyakit)
        btnHasilDiagnosa = findViewById(R.id.btnHasilDiagnosa)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        rvGejalaPenyakit.setLayoutManager(LinearLayoutManager(this))
        konsultasiAdapter = KonsultasiAdapter(this, modelKonsultasiArrayList)
        rvGejalaPenyakit.setAdapter(konsultasiAdapter)
        rvGejalaPenyakit.setHasFixedSize(true)
        btnHasilDiagnosa.setOnClickListener(View.OnClickListener { v ->
            val gejalaTerpilih = StringBuffer()
            val gejalaList: ArrayList<ModelKonsultasi> = modelKonsultasiArrayList
            for (i in gejalaList.indices) {
                val gejala: ModelKonsultasi = gejalaList[i]
                if (gejala.isSelected()) {
                    gejalaTerpilih.append(gejala.getStrGejala()).append("#")
                }
            }
            if (gejalaTerpilih.toString() == "") {
                Toast.makeText(
                    this@KonsultasiActivity,
                    "Silakan pilih gejala dahulu!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                // tampilkan activity hasil diagnosa
                val intent = Intent(
                    v.context,
                    HasilDiagnosaActivity::class.java
                )
                intent.putExtra("HASIL", gejalaTerpilih.toString())
                startActivity(intent)
            }
        })
        listData
    }

    private val listData: Unit
        get() {
            modelKonsultasiArrayList = databaseHelper?.getDaftarGejala() ?: ArrayList()
            if (modelKonsultasiArrayList.isEmpty()) {
                rvGejalaPenyakit.visibility = View.GONE
            } else {
                rvGejalaPenyakit.visibility = View.VISIBLE
                konsultasiAdapter = KonsultasiAdapter(this, modelKonsultasiArrayList)
                rvGejalaPenyakit.adapter = konsultasiAdapter
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        listData
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

