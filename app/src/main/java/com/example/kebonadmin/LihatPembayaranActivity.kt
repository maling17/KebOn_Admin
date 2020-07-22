package com.example.kebonadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebonadmin.adapter.LihatProdukAdapter
import com.example.kebonadmin.fragment.BeliPembayaranFragment
import com.example.kebonadmin.fragment.JasaPembayaranFragment
import com.example.kebonadmin.model.Transaksi
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail_pembayaran.*
import kotlinx.android.synthetic.main.activity_lihat_pembayaran.*
import kotlinx.android.synthetic.main.fragment_beli_pembayaran.*

class LihatPembayaranActivity : AppCompatActivity() {
    private var transaksiList = ArrayList<Transaksi>()
    private var getUsername = ""

    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lihat_pembayaran)

        val beliFragment=BeliPembayaranFragment()
        val jasaFragment= JasaPembayaranFragment()
        setFragment(beliFragment)

        btn_beli_keranjang.setOnClickListener {
            changeBackground(btn_beli_keranjang, R.drawable.left_rounded_button_orange)
            changeBackground(btn_jasa_keranjang, R.drawable.right_rounded_button_cream)

            setFragment(beliFragment)
        }
        btn_jasa_keranjang.setOnClickListener {
            changeBackground(btn_beli_keranjang, R.drawable.left_rounded_button_cream)
            changeBackground(btn_jasa_keranjang, R.drawable.right_rounded_button_orange)
            setFragment(jasaFragment)
        }

    }

    companion object {
        private const val TAG = "MyActivity"
    }
    private fun setFragment(fragment: Fragment) {

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fl_main_list_pembayaran, fragment)
        fragmentTransaction.commit()
    }

    private fun changeBackground(button: LinearLayout, int: Int) {
        button.setBackgroundResource(int)
    }

}