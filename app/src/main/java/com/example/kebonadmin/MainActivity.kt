package com.example.kebonadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_tambah.setOnClickListener {
            val intent=Intent(this,TambahProdukActivity::class.java)
            startActivity(intent)
        }
        btn_lihat_produk.setOnClickListener {
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show()
        }
        btn_konfirmasi.setOnClickListener {
            val intent=Intent(this,LihatPembayaranActivity::class.java)
            startActivity(intent)
        }
        btn_perkembangan.setOnClickListener {
            val intent=Intent(this,ListPerkembanganActivity::class.java)
            startActivity(intent)
        }
    }
}