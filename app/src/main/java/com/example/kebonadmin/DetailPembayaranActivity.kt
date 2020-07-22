package com.example.kebonadmin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebonadmin.adapter.ProdukKonfirmasiAdapter
import com.example.kebonadmin.model.Detail_Transaksi
import com.example.kebonadmin.model.Transaksi
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_pembayaran.*

class DetailPembayaranActivity : AppCompatActivity() {
    private var produkList = ArrayList<Detail_Transaksi>()
    private var transaksiList = ArrayList<Transaksi>()

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences
    private var getUsername = ""
    private var total = ""
    private var tokenImage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pembayaran)

        produkList.clear()
        transaksiList.clear()

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mFirebaseDatabase = mFirebaseInstance.getReference("Users")
        preferences = Preferences(applicationContext)

        getUsername = preferences.getValues("username").toString()
        val getIdTransaksi = preferences.getValues("id_transaksi").toString()

        getDataTransaksi()
        getDataDetailTransaksi()

        changeSizeImage(iv_bukti_pembayaran, 1000, 700)

        rv_produk_konfirmasi.layoutManager = LinearLayoutManager(this)

        btn_lanjut_konfirmasi.setOnClickListener {
            queryUpdate(getIdTransaksi, "status_beli", "4")
            finish()
        }
        iv_bukti_pembayaran.setOnClickListener {
            when (tokenImage) {
                0 -> {
                    tokenImage = 1
                    changeSizeImage(iv_bukti_pembayaran, 1000, 700)
                }
                1 -> {
                    tokenImage = 0
                    changeSizeImage(
                        iv_bukti_pembayaran,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                }

            }
        }

    }

    private fun getDataTransaksi() {

        val getIdTransaksi = preferences.getValues("id_transaksi").toString()
        val mDatabaseIdTransaksi =
            mDatabase.child("Users").child(getUsername).child("Transaksi").child(getIdTransaksi)

        mDatabaseIdTransaksi.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPembayaranActivity,
                    "DATA TIDAK ADA",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {

                val getNmAlamat =
                    p0.child("nm_alamat").value.toString()
                val getNmrAlamat =
                    p0.child("nmr_telp").value.toString()
                val getAlamatLengkap =
                    p0.child("alamat_lengkap").value.toString()
                val getNamaKurir = p0.child("jenis_pengiriman").value.toString()
                val getHargaKurir =
                    p0.child("subtotal_pengiriman").value.toString()
                val getSubtotalProduk =
                    p0.child("subtotal_produk_beli").value.toString()
                val getTotal = p0.child("total_biaya").value.toString()
                val urlPhotoBukti = p0.child("url_bukti_pembayaran").value.toString()

                tv_nama_alamat_pengiriman.text = getNmAlamat
                tv_nmr_alamat_pengiriman.text = getNmrAlamat
                tv_alamat_lengkap.text = getAlamatLengkap
                tv_jenis_pengiriman.text = getNamaKurir
                tv_harga_ongkir_konfirmasi.text = "Rp$getHargaKurir"
                tv_subtotal_produk.text = "Rp$getSubtotalProduk"
                tv_subtotal_pengiriman.text = "Rp$getHargaKurir"
                tv_total_pembayaran_checkout.text = "Rp$getTotal"
                tv_total_semua_konfirmasi.text = "Rp$getTotal"
                Picasso.get().load(urlPhotoBukti).into(iv_bukti_pembayaran)

                Log.d(TAG, "onDataChange: $getAlamatLengkap ")

            }

        })
    }


    private fun getDataDetailTransaksi() {

        val getIdTransaksi = preferences.getValues("id_transaksi").toString()

        // request data di detail transaksi
        val mDatabaseStarter =
            mDatabase.child("Users").child(getUsername).child("Transaksi")
                .child(getIdTransaksi).child("Detail_Transaksi")

        mDatabaseStarter.addListenerForSingleValueEvent(object :
            ValueEventListener {

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    for (dataSnapshot in p0.children) {

                        val transaksi =
                            dataSnapshot.getValue(Detail_Transaksi::class.java)
                        produkList.add(transaksi!!)
                        total =
                            (produkList.sumBy { it.harga_beli?.toInt()!! }).toString()
                    }

                    tv_total_produk_beli.text = "Rp$total"

                    rv_produk_konfirmasi.adapter =
                        ProdukKonfirmasiAdapter(produkList) {
                        }

                } else {
                    Toast.makeText(
                        this@DetailPembayaranActivity,
                        "Data Tidak Data Yang INI",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPembayaranActivity,
                    "" + p0.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })


    }

    private fun queryUpdate(id: String, node: String, value: String) {
        mDatabase.child("Users").child(getUsername)
            .child("Transaksi")
            .child(id).child(node)
            .setValue(value)

    }

    companion object {
        private const val TAG = "MyActivity"
    }

    private fun changeSizeImage(image: ImageView, panjang: Int, lebar: Int) {

        image.layoutParams.height = panjang
        image.layoutParams.width = lebar
        image.requestLayout()

    }

}