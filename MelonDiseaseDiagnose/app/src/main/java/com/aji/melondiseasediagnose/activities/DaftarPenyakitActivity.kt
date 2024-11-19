package com.aji.melondiseasediagnose.activities

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aji.melondiseasediagnose.R
import com.aji.melondiseasediagnose.adapter.DaftarPenyakitAdapter
import com.aji.melondiseasediagnose.db.DatabaseHelper
import com.aji.melondiseasediagnose.model.ModelDaftarPenyakit

class DaftarPenyakitActivity : AppCompatActivity() {
    private var sqLiteDatabase: SQLiteDatabase? = null
    private var databaseHelper: DatabaseHelper? = null
    private var modelDaftarPenyakitList = ArrayList<ModelDaftarPenyakit>()
    private var daftarPenyakitAdapter: DaftarPenyakitAdapter? = null
    private var rvDaftarPenyakit: RecyclerView? = null
    private var toolbar: Toolbar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_penyakit)
        setStatusBar()
        toolbar = findViewById(R.id.toolbar)
        rvDaftarPenyakit = findViewById(R.id.rvDaftarPenyakit)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }

        //set database
        databaseHelper = DatabaseHelper(this)
        if (databaseHelper!!.openDatabase()) sqLiteDatabase = databaseHelper!!.readableDatabase
        rvDaftarPenyakit?.layoutManager = LinearLayoutManager(this)
        daftarPenyakitAdapter = DaftarPenyakitAdapter(this, modelDaftarPenyakitList)
        rvDaftarPenyakit?.adapter = DaftarPenyakitAdapter(this, modelDaftarPenyakitList)
        rvDaftarPenyakit?.setHasFixedSize(true)
        listData
    }

    private val listData: Unit
        get() {
            modelDaftarPenyakitList = databaseHelper!!.getDaftarPenyakit()
            if (modelDaftarPenyakitList.size == 0) {
                rvDaftarPenyakit!!.visibility = View.GONE
            } else {
                rvDaftarPenyakit!!.visibility = View.VISIBLE
                daftarPenyakitAdapter = DaftarPenyakitAdapter(this, modelDaftarPenyakitList)
                rvDaftarPenyakit!!.adapter = daftarPenyakitAdapter
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
