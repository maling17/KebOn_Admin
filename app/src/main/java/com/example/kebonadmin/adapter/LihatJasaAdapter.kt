package com.example.kebonadmin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kebonadmin.DetailPembayaranActivity
import com.example.kebonadmin.R
import com.example.kebonadmin.model.Jasa
import com.example.kebonadmin.model.Transaksi
import com.example.kebonadmin.utils.Preferences

class LihatJasaAdapter(
    private var data: List<Jasa>,
    private val listener: (Jasa) -> Unit
) : RecyclerView.Adapter<LihatJasaAdapter.LeagueViewHolder>() {
    lateinit var ContextAdapter: Context
    var total: Int = 0

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var preferences: Preferences

        private var getUsername = ""


        private val tvIdTransaksi: TextView = view.findViewById(R.id.tv_id_transaksi)
        private val tvUsername: TextView = view.findViewById(R.id.tv_username_pembeli)
        private val tvtotal: TextView = view.findViewById(R.id.tv_total_pembayaran)
        private val tvTglPembayaran: TextView = view.findViewById(R.id.tv_tgl_pembayaran)


        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: Jasa,
            listener: (Jasa) -> Unit,
            context: Context,
            position: Int
        ) {

            tvIdTransaksi.text = data.id_jasa
            tvtotal.text = "Rp" + data.total_biaya_jasa.toString()
            tvTglPembayaran.text = data.tgl_jasa.toString()
            tvUsername.text = data.username.toString()
            preferences = Preferences(context)
            getUsername = data.username.toString()

            itemView.setOnClickListener {
                listener(data)

                preferences.setValues("username",data.username.toString() )
                preferences.setValues("id_jasa",data.id_jasa.toString() )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View =
            layoutInflater.inflate(R.layout.list_pembayaran_item, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)

    }

}