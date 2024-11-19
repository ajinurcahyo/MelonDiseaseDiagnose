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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.aji.melondiseasediagnose.R
import com.aji.melondiseasediagnose.db.DatabaseHelper
import com.google.android.material.button.MaterialButton
import java.util.Collections
import java.util.LinkedList


class HasilDiagnosaActivity : AppCompatActivity() {
    var sqLiteDatabase: SQLiteDatabase? = null
    var toolbar: Toolbar? = null
    private lateinit var tvGejala: TextView
    private lateinit var tvNamaPenyakit: TextView
    private lateinit var btnDiagnosaUlang: MaterialButton
    private lateinit var btnDaftarPenyakit: MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hasil_diagnosa)
        setStatusBar()
        val databaseHelper = DatabaseHelper(this)
        if (databaseHelper.openDatabase()) sqLiteDatabase = databaseHelper.readableDatabase
        toolbar = findViewById(R.id.toolbar)
        tvGejala = findViewById(R.id.tvGejala)
        tvNamaPenyakit = findViewById(R.id.tvNamaPenyakit)
        btnDiagnosaUlang = findViewById(R.id.btnDiagnosaUlang)
        btnDaftarPenyakit = findViewById(R.id.btnDaftarPenyakit)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowTitleEnabled(false)
        }
        val str_hasil = intent.getStringExtra("HASIL")
        var gejala_terpilih = arrayOfNulls<String>(0)
        if (str_hasil != null) {
            gejala_terpilih =
                str_hasil.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        }
        var cf_gabungan: Double
        var cf: Double
        val mapHasil = HashMap<String, Double>()
        val query_penyakit = "SELECT kode_penyakit FROM penyakit order by kode_penyakit"
        val cursor_penyakit = sqLiteDatabase!!.rawQuery(query_penyakit, null)
        while (cursor_penyakit.moveToNext()) {
            cf_gabungan = 0.0
            var i = 0
            val query_rule =
                "SELECT nilai_cf, kode_gejala FROM rule where kode_penyakit = '" + cursor_penyakit.getString(
                    0
                ) + "'"
            val cursor_rule = sqLiteDatabase!!.rawQuery(query_rule, null)
            while (cursor_rule.moveToNext()) {
                cf = cursor_rule.getDouble(0)
                for (s_gejala_terpilih in gejala_terpilih) {
                    val query_gejala =
                        "SELECT kode_gejala FROM gejala where nama_gejala = '$s_gejala_terpilih'"
                    val cursor_gejala = sqLiteDatabase!!.rawQuery(query_gejala, null)
                    cursor_gejala.moveToFirst()
                    if (cursor_rule.getString(1) == cursor_gejala.getString(0)) {
                        cf_gabungan = if (i > 1) {
                            cf + cf_gabungan * (1 - cf)
                        } else if (i == 1) {
                            cf_gabungan + cf * (1 - cf_gabungan)
                        } else {
                            cf
                        }
                        i++
                    }
                    cursor_gejala.close()
                }
            }
            cursor_rule.close()
            mapHasil[cursor_penyakit.getString(0)] = cf_gabungan * 100
        }
        cursor_penyakit.close()
        val output_gejala_terpilih = StringBuffer()
        var no = 1
        for (s_gejala_terpilih in gejala_terpilih) {
            output_gejala_terpilih.append(no++)
                .append(". ")
                .append(s_gejala_terpilih)
                .append("\n")
        }
        tvGejala.setText(output_gejala_terpilih)
        val sortedHasil: Map<String, Double> = sortByValue(mapHasil)
        val (kode_penyakit, hasil_cf) = sortedHasil.entries.iterator().next()
        val persentase = hasil_cf.toInt()
        val query_penyakit_hasil =
            "SELECT nama_penyakit FROM penyakit where kode_penyakit='$kode_penyakit'"
        val cursor_hasil = sqLiteDatabase!!.rawQuery(query_penyakit_hasil, null)
        cursor_hasil.moveToFirst()
        tvNamaPenyakit.setText(cursor_hasil.getString(0) + " " + persentase + "%")
        cursor_hasil.close()
        btnDiagnosaUlang.setOnClickListener(View.OnClickListener { finish() })
        btnDaftarPenyakit.setOnClickListener(View.OnClickListener {
            val intent = Intent(
                this@HasilDiagnosaActivity,
                DaftarPenyakitActivity::class.java
            )
            startActivity(intent)
        })
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
        fun sortByValue(hm: HashMap<String, Double>): HashMap<String, Double> {
            val list: List<Map.Entry<String, Double>> = LinkedList(hm.entries)

            // Menggunakan Comparator dengan parameter yang benar
            Collections.sort(list, object : Comparator<Map.Entry<String, Double>> {
                override fun compare(
                    o1: Map.Entry<String, Double>,
                    o2: Map.Entry<String, Double>
                ): Int {
                    return o2.value.compareTo(o1.value)
                }
            })

            val temp = LinkedHashMap<String, Double>()
            for ((key, value) in list) {
                temp[key] = value
            }
            return temp
        }

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

