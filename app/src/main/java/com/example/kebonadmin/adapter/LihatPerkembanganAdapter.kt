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
import com.example.kebonadmin.model.Perkembangan_Tanaman
import com.example.kebonadmin.model.Transaksi
import com.example.kebonadmin.utils.Preferences

class LihatPerkembanganAdapter(
    private var data: List<Perkembangan_Tanaman>,
    private val listener: (Perkembangan_Tanaman) -> Unit
) : RecyclerView.Adapter<LihatPerkembanganAdapter.LeagueViewHolder>() {
    lateinit var ContextAdapter: Context
    var total: Int = 0

    class LeagueViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private lateinit var preferences: Preferences
        private var getUsername = ""

        private val tvIdPerkembangan: TextView = view.findViewById(R.id.tv_id_perkembangan)
        private val tvUsername: TextView = view.findViewById(R.id.tv_username_pembeli_perkembangan)
        private val tvStatus: TextView = view.findViewById(R.id.tv_status_perkembangan)
        private val tvTglUpdate: TextView = view.findViewById(R.id.tv_tgl_perkembangan)
        private val tvNamaTanaman: TextView = view.findViewById(R.id.tv_nama_tanaman)


        @SuppressLint("SetTextI18n")
        fun bindItem(
            data: Perkembangan_Tanaman,
            listener: (Perkembangan_Tanaman) -> Unit,
            context: Context,
            position: Int
        ) {

            tvIdPerkembangan.text=data.id_perkembangan
            tvNamaTanaman.text=data.nm_tanaman
            tvStatus.text=data.status_perkembangan
            tvUsername.text=data.username
            tvTglUpdate.text=data.tgl_update

            itemView.setOnClickListener {
                listener(data)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeagueViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        ContextAdapter = parent.context
        val inflatedView: View =
            layoutInflater.inflate(R.layout.list_perkembangan_item, parent, false)

        return LeagueViewHolder(inflatedView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: LeagueViewHolder, position: Int) {
        holder.bindItem(data[position], listener, ContextAdapter, position)

    }

}