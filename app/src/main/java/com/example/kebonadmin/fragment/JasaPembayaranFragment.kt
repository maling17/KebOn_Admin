package com.example.kebonadmin.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebonadmin.DetailPembayaranJasaActivity
import com.example.kebonadmin.R
import com.example.kebonadmin.adapter.LihatJasaAdapter
import com.example.kebonadmin.model.Jasa
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_jasa_pembayaran.*


class JasaPembayaranFragment : Fragment() {
    private var jasaList = ArrayList<Jasa>()
    private var getUsername = ""

    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jasa_pembayaran, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        jasaList.clear()
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        preferences = Preferences(activity!!.applicationContext)

        getDataTransaksi()
        refreshState()

        if (jasaList.isEmpty()) {
            val handler = Handler()
            handler.postDelayed({

                Toast.makeText(context, "Data tidak ada", Toast.LENGTH_SHORT).show()
                pb_jasa_pembayaran.visibility = View.GONE
            }, 8000)
            pb_jasa_pembayaran.visibility = View.VISIBLE

        } else {

            rv_lihat_jasa_produk.visibility = View.VISIBLE
        }

        rv_lihat_jasa_produk.layoutManager = LinearLayoutManager(context)
    }

    private fun getDataTransaksi() {

        val mDatabaseUsername =
            mDatabase.child("Users")

        mDatabaseUsername.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context, "DATA TIDAK ADA", Toast.LENGTH_SHORT)
                    .show()

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (getIddataSnapshot in p0.children) {
                    getUsername = getIddataSnapshot.child("username").value.toString()

                    val mDatabaseIdTransaksi =
                        mDatabase.child("Users").child(getUsername).child("Jasa")
                            .orderByChild("status_jasa").equalTo("3")

                    mDatabaseIdTransaksi.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                context,
                                "DATA TIDAK ADA",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (getdataSnapshot in p0.children) {
                                val jasa = getdataSnapshot.getValue(Jasa::class.java)
                                jasaList.add(jasa!!)

                            }

                            rv_lihat_jasa_produk.adapter = LihatJasaAdapter(jasaList) {

                                val intent = Intent(
                                    context,
                                    DetailPembayaranJasaActivity::class.java
                                )
                                startActivity(intent)

                            }

                        }


                    })
                }

            }

        })


    }

    private fun refreshState() {
        //  untuk merefresh data
        srl_lihat_jasa_pembayaran.setOnRefreshListener {

            jasaList.clear()
            val handler = Handler()
            handler.postDelayed({
                // ambil data - data
                getDataTransaksi()

                if (jasaList.isEmpty()) {
                    pb_jasa_pembayaran.visibility = View.VISIBLE

                } else {
                    pb_jasa_pembayaran.visibility = View.GONE
                    rv_lihat_jasa_produk.visibility = View.VISIBLE
                }
                rv_lihat_jasa_produk.layoutManager = LinearLayoutManager(context)


                if (srl_lihat_jasa_pembayaran.isRefreshing) {
                    srl_lihat_jasa_pembayaran.isRefreshing = false
                }
            }, 2000)
        }

    }
}