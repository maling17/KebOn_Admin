package com.example.kebonadmin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kebonadmin.adapter.LihatPerkembanganAdapter
import com.example.kebonadmin.model.Perkembangan_Tanaman
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list_perkembangan.*

class ListPerkembanganActivity : AppCompatActivity() {
    private var perkembanganList = ArrayList<Perkembangan_Tanaman>()
    private var getUsername = ""

    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_perkembangan)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        preferences = Preferences(this)
        getDataTransaksi()

        val handler = Handler()
        handler.postDelayed({
            pb_perkembangan.visibility = View.GONE
        }, 3000)

        rv_list_perkembangan.layoutManager = LinearLayoutManager(this)

    }

    private fun getDataTransaksi() {

        val mDatabaseUsername =
            mDatabase.child("Users")

        mDatabaseUsername.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@ListPerkembanganActivity, "DATA TIDAK ADA", Toast.LENGTH_SHORT)
                    .show()

            }

            override fun onDataChange(p0: DataSnapshot) {

                for (getIddataSnapshot in p0.children) {
                    getUsername = getIddataSnapshot.child("username").value.toString()

                    val mDatabasePerkembangan =
                        mDatabase.child("Users").child(getUsername).child("Perkembangan_Tanaman")
                    mDatabasePerkembangan.addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@ListPerkembanganActivity,
                                "DATA TIDAK ADA",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                        override fun onDataChange(p0: DataSnapshot) {

                            for (getDataSnapshot in p0.children) {
                                val perkembanganTanaman =
                                    getDataSnapshot.getValue(Perkembangan_Tanaman::class.java)
                                perkembanganList.add(perkembanganTanaman!!)
                            }
                            rv_list_perkembangan.adapter =
                                LihatPerkembanganAdapter(perkembanganList) {
                                    val intent = Intent(
                                        this@ListPerkembanganActivity,
                                        DetailPerkembanganTanaman::class.java
                                    )
                                    intent.putExtra("id_perkembangan", it.id_perkembangan)
                                    intent.putExtra("username",it.username)
                                    startActivity(intent)

                                }

                        }

                    })
                }
            }
        })
    }
}