package com.aji.melondiseasediagnose.activities

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aji.melondiseasediagnose.R
import com.aji.melondiseasediagnose.db.DatabaseHelper


class DetailPenyakitActivity : AppCompatActivity() {
    private var sqLiteDatabase: SQLiteDatabase? = null
    private var databaseHelper: DatabaseHelper? = null
    private var toolbar: Toolbar? = null
    private lateinit var tvTitle: TextView
    private lateinit var tvPenjelasan: TextView
    private lateinit var tvPenanganan: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_penyakit)
        setStatusBar()

        //set database
        databaseHelper = DatabaseHelper(this)
        if (databaseHelper!!.openDatabase()) sqLiteDatabase = databaseHelper!!.readableDatabase
        val strKodePenyakit = intent.getStringExtra("KODE_PENYAKIT")
        val selectQuery =
            "SELECT nama_penyakit, deskripsi, penanganan FROM penyakit WHERE kode_penyakit = '$strKodePenyakit'"
        val cursor = sqLiteDatabase!!.rawQuery(selectQuery, null)
        cursor.moveToFirst()
        toolbar = findViewById(R.id.toolbar)
        tvTitle = findViewById(R.id.tvTitle)
        tvPenjelasan = findViewById(R.id.tvPenjelasan)
        tvPenanganan = findViewById(R.id.tvPenanganan)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        tvTitle.text = cursor.getString(0)
        tvPenjelasan.text = cursor.getString(1)
        tvPenanganan.text = cursor.getString(2)
        cursor.close()
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

