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
import com.example.kebonadmin.DetailPembayaranActivity
import com.example.kebonadmin.R
import com.example.kebonadmin.adapter.LihatProdukAdapter
import com.example.kebonadmin.model.Transaksi
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_beli_pembayaran.*

class BeliPembayaranFragment : Fragment() {
    private var transaksiList = ArrayList<Transaksi>()
    private var getUsername = ""

    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_beli_pembayaran, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        transaksiList.clear()
        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        preferences = Preferences(activity!!.applicationContext)

        getDataTransaksi()
        refreshState()

        if(transaksiList.isEmpty()){
            pb_beli_pembayaran.visibility=View.VISIBLE
        }else{
            pb_beli_pembayaran.visibility=View.GONE
            rv_lihat_beli_produk.visibility=View.VISIBLE
        }

        rv_lihat_beli_produk.layoutManager = LinearLayoutManager(context)

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
                        mDatabase.child("Users").child(getUsername).child("Transaksi")
                            .orderByChild("status_beli").equalTo("3")

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
                                val transaksi = getdataSnapshot.getValue(Transaksi::class.java)
                                transaksiList.add(transaksi!!)

                            }

                            rv_lihat_beli_produk.adapter = LihatProdukAdapter(transaksiList) {

                                val intent = Intent(
                                    context,
                                    DetailPembayaranActivity::class.java
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
        srl_lihat_pembayaran.setOnRefreshListener {

            transaksiList.clear()
            val handler = Handler()
            handler.postDelayed({
                // ambil data - data
                getDataTransaksi()

                if(transaksiList.isEmpty()){
                    pb_beli_pembayaran.visibility=View.VISIBLE

                }else{
                    pb_beli_pembayaran.visibility=View.GONE
                    rv_lihat_beli_produk.visibility=View.VISIBLE
                }
                rv_lihat_beli_produk.layoutManager = LinearLayoutManager(context)


                if (srl_lihat_pembayaran.isRefreshing) {
                    srl_lihat_pembayaran.isRefreshing = false
                }
            }, 2000)
        }

    }
}