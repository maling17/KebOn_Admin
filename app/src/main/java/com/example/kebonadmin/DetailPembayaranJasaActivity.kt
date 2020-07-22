package com.example.kebonadmin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebonadmin.adapter.ProdukJasaKonfirmasiAdapter
import com.example.kebonadmin.model.Detail_Jasa
import com.example.kebonadmin.model.Jasa
import com.example.kebonadmin.model.Perkembangan_Tanaman
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_pembayaran_jasa.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DetailPembayaranJasaActivity : AppCompatActivity() {
    private var produkList = ArrayList<Detail_Jasa>()
    private var transaksiList = ArrayList<Jasa>()

    private lateinit var mFirebaseDatabase: DatabaseReference
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences
    private var getUsername = ""
    private var total = ""
    private var tokenImage = 0
    private var nmProduk = ""
    private var tglMulai = ""
    private var urlGambar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_pembayaran_jasa)

        produkList.clear()
        transaksiList.clear()

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        mFirebaseDatabase = mFirebaseInstance.getReference("Users")
        preferences = Preferences(applicationContext)

        getUsername = preferences.getValues("username").toString()
        val getIdJasa = preferences.getValues("id_jasa").toString()

        getDataTransaksi()
        getDataDetailTransaksi()

        changeSizeImage(iv_bukti_pembayaran_jasa, 1000, 700)

        rv_produk_jasa_konfirmasi.layoutManager = LinearLayoutManager(this)

        btn_lanjut_konfirmasi_jasa.setOnClickListener {
            queryUpdate(getIdJasa, "status_jasa", "4")
            simpanDataPerkembangan()
            finish()
        }
        iv_bukti_pembayaran_jasa.setOnClickListener {
            when (tokenImage) {
                0 -> {
                    tokenImage = 1
                    changeSizeImage(iv_bukti_pembayaran_jasa, 1000, 700)
                }
                1 -> {
                    tokenImage = 0
                    changeSizeImage(
                        iv_bukti_pembayaran_jasa,
                        ConstraintLayout.LayoutParams.MATCH_PARENT,
                        ConstraintLayout.LayoutParams.MATCH_PARENT
                    )
                }

            }
        }

    }

    private fun getDataTransaksi() {

        val getIdJasa = preferences.getValues("id_jasa").toString()
        val mDatabaseIdTransaksi =
            mDatabase.child("Users").child(getUsername).child("Jasa").child(getIdJasa)

        mDatabaseIdTransaksi.addListenerForSingleValueEvent(object :
            ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPembayaranJasaActivity,
                    "DATA TIDAK ADA",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }

            @RequiresApi(Build.VERSION_CODES.O)
            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {

                val getDurasiPerawatan = p0.child("durasi_perawatan").value.toString()
                val getTglJasa = p0.child("tgl_jasa").value.toString()
                val getHargaDurasi =
                    p0.child("subtotal_perawatan").value.toString()
                val getSubtotalProduk =
                    p0.child("subtotal_produk_jasa").value.toString()
                val getTotal = p0.child("total_biaya_jasa").value.toString()
                val urlPhotoBukti = p0.child("url_bukti_pembayaran_jasa").value.toString()
                tglMulai = p0.child("tgl_jasa").value.toString()

                val format=DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                val mDurasi=LocalDateTime.parse(getDurasiPerawatan,format)
                val mTglJasa=LocalDateTime.parse(getTglJasa,format)

                val diffDate=ChronoUnit.MONTHS.between(mTglJasa,mDurasi)

                tv_durasi_perawatan.text ="$diffDate Bulan"
                tv_harga_durasi_perawatan.text = "Rp$getHargaDurasi"
                tv_subtotal_produk_jasa.text = "Rp$getSubtotalProduk"
                tv_subtotal_perawatan.text = "Rp$getHargaDurasi"
                tv_total_pembayaran_jasa.text = "Rp$getTotal"
                tv_total_semua_konfirmasi_jasa.text = "Rp$getTotal"
                tv_total_produk_beli_jasa.text = "Rp$getTotal"
                Picasso.get().load(urlPhotoBukti).into(iv_bukti_pembayaran_jasa)

            }

        })
    }

    private fun getDataDetailTransaksi() {

        val getIdJasa = preferences.getValues("id_jasa").toString()

        // request data di detail transaksi
        val mDatabaseStarter =
            mDatabase.child("Users").child(getUsername).child("Jasa")
                .child(getIdJasa).child("Detail_Jasa")

        mDatabaseStarter.addListenerForSingleValueEvent(object :
            ValueEventListener {

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {

                    for (dataSnapshot in p0.children) {

                        val jasa =
                            dataSnapshot.getValue(Detail_Jasa::class.java)
                        produkList.add(jasa!!)
                        val idProduk = dataSnapshot.child("id_produk").value.toString()

                        val mDatabaseProduk =
                            mDatabase.child("Produk").orderByChild("id_produk").equalTo(idProduk)

                        mDatabaseProduk.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Toast.makeText(
                                    this@DetailPembayaranJasaActivity,
                                    p0.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                nmProduk = p0.child("nm_produk").value.toString()

                            }
                        })

                        total =
                            (produkList.sumBy { it.harga_beli?.toInt()!! }).toString()

                    }

                    tv_total_pembayaran_jasa.text = "Rp$total"

                    rv_produk_jasa_konfirmasi.adapter =
                        ProdukJasaKonfirmasiAdapter(produkList) {
                        }

                } else {
                    Toast.makeText(
                        this@DetailPembayaranJasaActivity,
                        "Data Tidak Data Yang INI",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPembayaranJasaActivity,
                    "" + p0.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })


    }

    private fun queryUpdate(id: String, node: String, value: String) {
        mDatabase.child("Users").child(getUsername)
            .child("Jasa")
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

    private fun simpanDataPerkembangan() {

        val getIdJasa = preferences.getValues("id_jasa").toString()

        // request data di detail transaksi
        val mDatabaseStarter =
            mDatabase.child("Users").child(getUsername).child("Jasa")
                .child(getIdJasa).child("Detail_Jasa")

        mDatabaseStarter.addListenerForSingleValueEvent(object :
            ValueEventListener {

            @SuppressLint("SetTextI18n")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(p0: DataSnapshot) {

                if (p0.exists()) {
                    for (dataSnapshot in p0.children) {

                        val idProduk = dataSnapshot.child("id_produk").value.toString()

                        val mDatabaseProduk =
                            mDatabase.child("Produk").orderByChild("id_produk").equalTo(idProduk)

                        mDatabaseProduk.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Toast.makeText(
                                    this@DetailPembayaranJasaActivity,
                                    p0.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                for (getDataSnapshot in p0.children) {

                                    nmProduk = getDataSnapshot.child("nm_produk").value.toString()
                                    urlGambar = getDataSnapshot.child("url").value.toString()

                                }
                                Toast.makeText(
                                    this@DetailPembayaranJasaActivity,
                                    idProduk,
                                    Toast.LENGTH_SHORT
                                ).show()

                                 val data = Perkembangan_Tanaman()
                                data.deskripsi_tanaman = "Tanaman baru saja ditanam"
                                data.estimasi_panen = " "
                                data.id_jasa = getIdJasa
                                val key =
                                    mFirebaseDatabase.child(getUsername)
                                        .child("Perkembangan_Tanaman")
                                        .push().key
                                data.id_perkembangan = key.toString()
                                data.nm_tanaman = nmProduk
                                data.tgl_mulai = tglMulai
                                data.tgl_update = " "
                                data.url_perkembangan = urlGambar
                                data.tambah_durasi="0 Bulan"
                                data.status_perkembangan = "Baru saja Ditanam"

                                mFirebaseDatabase.child(getUsername).child("Perkembangan_Tanaman")
                                    .child(key.toString())
                                    .setValue(data)

                            }
                        })
                    }
                } else {
                    Toast.makeText(
                        this@DetailPembayaranJasaActivity,
                        "Data Tidak Data Yang INI",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPembayaranJasaActivity,
                    "" + p0.message,
                    Toast.LENGTH_LONG
                ).show()
            }

        })


    }

}