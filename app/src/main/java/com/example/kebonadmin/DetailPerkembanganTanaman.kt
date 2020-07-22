package com.example.kebonadmin

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.kebonadmin.model.Perkembangan_Tanaman
import com.example.kebonadmin.model.Produk
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_perkembangan_tanaman.*
import kotlinx.android.synthetic.main.activity_tambah_produk.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.collections.ArrayList

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DetailPerkembanganTanaman : AppCompatActivity() {
    private var perkembanganList = ArrayList<Perkembangan_Tanaman>()


    val REQUEST_CODE = 100
    private lateinit var mFirebaseInstance: FirebaseDatabase
    private lateinit var mDatabase: DatabaseReference
    private lateinit var preferences: Preferences
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var filePath: Uri
    var getUrl = ""


    private var nmTanaman = ""
    private var status = " "
    private var estimasi_panen = " "
    private var deskripsi_pertumbuhan = ""
    private var url_photo = ""
    private var idPerkembangan = ""
    private var tglMulai = ""
    private var tglUpdate = ""
    private var idJasa = ""
    private var getUsername = ""
    private var tambahDurasi = ""

    private var getDurasiJasa = ""
    private var diffDate = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_perkembangan_tanaman)

        mFirebaseInstance = FirebaseDatabase.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        idPerkembangan = intent.getStringExtra("id_perkembangan").toString()
        getUsername = intent.getStringExtra("username").toString()

        getDataPerkembangan()
        decreementWaktuPerawatan()

        btn_tambah_photo_detail_perkembangan.setOnClickListener {
            pickImageFromGallery()
        }
        btn_simpan_detail_perkembangan.setOnClickListener { updateData()     }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            filePath = data!!.data!!

            btn_tambah_photo_detail_perkembangan.setImageURI(data.data) // handle chosen image
            btn_tambah_photo_detail_perkembangan.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
            btn_tambah_photo_detail_perkembangan.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            btn_tambah_photo_detail_perkembangan.setPadding(0, 0, 0, 0)
            btn_tambah_photo_detail_perkembangan.setBackgroundResource(0)
        }
    }

    private fun getDataPerkembangan() {

        val mDatabasePerkembangan =
            mDatabase.child("Users").child(getUsername).child("Perkembangan_Tanaman")
                .orderByChild("id_perkembangan").equalTo(idPerkembangan)

        mDatabasePerkembangan.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@DetailPerkembanganTanaman, p0.message, Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (getDataSnapshot in p0.children) {

                    nmTanaman = getDataSnapshot.child("nm_tanaman").value.toString()
                    status = getDataSnapshot.child("status_perkembangan").value.toString()
                    estimasi_panen = getDataSnapshot.child("estimasi_panen").value.toString()
                    deskripsi_pertumbuhan =
                        getDataSnapshot.child("deskripsi_tanaman").value.toString()
                    url_photo = getDataSnapshot.child("url_perkembangan").value.toString()
                    tglMulai=getDataSnapshot.child("tgl_mulai").value.toString()
                    idJasa=getDataSnapshot.child("id_jasa").value.toString()
                    tambahDurasi=getDataSnapshot.child("tambah_durasi").value.toString()

                    tv_jenis_tanaman.text = nmTanaman
                    tv_id_detail_perkembangan.text = idPerkembangan
                    tv_username_detail_perkembangan.text = getUsername

                    et_status.text = Editable.Factory.getInstance().newEditable(status)
                    et_estimasi_panen.text =
                        Editable.Factory.getInstance().newEditable(estimasi_panen)
                    et_deskripsi_pertumbuhan.text =
                        Editable.Factory.getInstance().newEditable(deskripsi_pertumbuhan)
                    et_status.text = Editable.Factory.getInstance().newEditable(status)

                    Picasso.get().load(url_photo).into(btn_tambah_photo_detail_perkembangan)

                }
            }
        })

    }

    private fun decreementWaktuPerawatan() {

        val mDatabasePerkembangan =
            mDatabase.child("Users").child(getUsername).child("Perkembangan_Tanaman")
                .orderByChild("id_perkembangan").equalTo(idPerkembangan)

        mDatabasePerkembangan.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(
                    this@DetailPerkembanganTanaman,
                    "DATA TIDAK ADA",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }

            @SuppressLint("SetTextI18n")
            override fun onDataChange(p0: DataSnapshot) {

                for (dataSnapshot in p0.children) {

                    val idJasa = dataSnapshot.child("id_jasa").value.toString()
                    val mDatabaseJasa =
                        mDatabase.child("Users").child(getUsername).child("Jasa")
                            .orderByChild("id_jasa").equalTo(idJasa)

                    mDatabaseJasa.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(
                                this@DetailPerkembanganTanaman,
                                "DATA JASA TIDAK ADA",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        @SuppressLint("SimpleDateFormat")
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun onDataChange(p0: DataSnapshot) {
                            for (getDataSnapshot in p0.children) {
                                getDurasiJasa =
                                    getDataSnapshot.child("durasi_perawatan").value.toString()
                            }
                            val currentDate = System.currentTimeMillis()
                            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                            val tglNow = dateFormat.format(currentDate)

                            val format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
                            val tglSKrg = LocalDateTime.parse(tglNow.toString(), format)
                            val tglDurasi = LocalDateTime.parse(getDurasiJasa, format)
                            diffDate = ChronoUnit.DAYS.between(tglSKrg, tglDurasi).toString()
                            tv_sisa_waktu.text = "$diffDate Hari"

                        }

                    })
                }

            }

        })


    }

    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, REQUEST_CODE)

    }
    private fun updateData() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        getUrl = ""
        val ref = storageReference.child("perkembangan/" + UUID.randomUUID().toString())
        val uploadTask = ref.putFile(filePath)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                val downloadUrl = task.result
                getUrl = downloadUrl.toString()

                val status=et_status.text
                val estimasi_panen=et_estimasi_panen.text
                val deskripsi=et_deskripsi_pertumbuhan.text

                val calendar= Calendar.getInstance()
                val currentDate = calendar.timeInMillis
                val dateFormat=SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
                val tglNow=dateFormat.format(currentDate)
                tglUpdate = tglNow.toString()

                val data=Perkembangan_Tanaman()
                data.tambah_durasi=tambahDurasi
                data.status_perkembangan=status.toString()
                data.url_perkembangan=getUrl
                data.tgl_update=tglUpdate
                data.nm_tanaman=nmTanaman
                data.tgl_mulai=tglMulai
                data.id_perkembangan=idPerkembangan
                data.username=getUsername
                data.estimasi_panen=estimasi_panen.toString()
                data.deskripsi_tanaman=deskripsi.toString()
                data.id_jasa=idJasa


                    mDatabase.child("Users").child(getUsername).child("Perkembangan_Tanaman")
                        .child(idPerkembangan).setValue(data)

                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "dapat url gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
}