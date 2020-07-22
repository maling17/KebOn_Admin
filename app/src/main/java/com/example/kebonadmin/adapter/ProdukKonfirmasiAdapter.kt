package com.example.kebonadmin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.kebonadmin.R
import com.example.kebonadmin.model.Detail_Transaksi
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class ProdukKonfirmasiAdapter(
    private var data: List<Detail_Transaksi>,
    private val listener: (Detail_Transaksi) -> Unit
) : RecyclerView.Adapter<ProdukKonfirmasiAdapter.LeagueViewHolder>() {
    lateinit var ContextAdapter: Context

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        lateinit var mDatabase: DatabaseReference
        lateinit var preferences: Preferences
        var nmProduk = ""
        var hargaBeliProduk = ""
        var urlGambar = ""
        var username: String = ""
        private val tvNamaProduk: TextView = view.findViewById(R.id.tv_nama_produk_detail_konfirmasi)
        private val tvJumlahProduk: TextView = view.findViewById(R.id.tv_jumlah_beli_produk_detail_konfirmasi)
        private val tvHargaProduk: TextView = view.findViewById(R.id.tv_harga_beli_produk_detail_konfirmasi)
        private val ivProduk: ImageView = view.findViewById(R.id.iv_produk_detail_konfirmasi)

        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: Detail_Transaksi,
            listener: (Detail_Transaksi) -> Unit,
            context: Context,
            position: Int
        ) {
            mDatabase = FirebaseDatabase.getInstance().reference
            preferences = Preferences(context)
            username = preferences.getValues("username").toString()
            val idProduk = data.id_produk.toString()

            val mDatabaseProduk =
                mDatabase.child("Produk").orderByChild("id_produk").equalTo(idProduk)
            mDatabaseProduk.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
                }

                override fun onDataChange(p0: DataSnapshot) {

                    for (dataSnapshot in p0.children) {
                        nmProduk = dataSnapshot.child("nm_produk").value.toString()
                        hargaBeliProduk = dataSnapshot.child("harga_beli").value.toString()
                        urlGambar = dataSnapshot.child("url").value.toString()
                    }

                    tvNamaProduk.text = nmProduk
                    Picasso.get().load(urlGambar).into(ivProduk)

                }

            })


            tvHargaProduk.text = "Rp" + data.harga_beli
            tvJumlahProduk.text = data.jumlah_beli
            itemView.setOnClickListener {
                listener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View =
            layoutInflater.inflate(R.layout.produk_konfirmasi_item, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)

    }

}