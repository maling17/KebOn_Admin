package com.example.kebonadmin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kebonadmin.model.Produk
import com.example.kebonadmin.utils.Preferences
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_tambah_produk.*
import java.util.*

class TambahProdukActivity : AppCompatActivity() {

    lateinit var mDatabase: DatabaseReference
    lateinit var preferences: Preferences
    private var newIdProduk = 0
    lateinit var storage: FirebaseStorage
    lateinit var storageReference: StorageReference
    lateinit var filePath: Uri

    val REQUEST_CODE = 100
    var getIdProduk = ""
    var getNmProduk = ""
    var getStok = ""
    var getMerk = ""
    var getDimensi = ""
    var getBerat = ""
    var getUkuran = ""
    var getKategori = ""
    var getPerawatan = ""
    var getCahaya = ""
    var getIsi = ""
    var getTipe = ""
    var getJenis = ""
    var getHargaBeli = ""
    var getHargaJual = ""
    var getDeskripsi = ""
    var getUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tambah_produk)

        mDatabase = FirebaseDatabase.getInstance().reference
        preferences = Preferences(this)
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference

        getIdProduk()
        btn_simpan.setOnClickListener {
            SimpanPhoto()
        }
        btn_tambah_photo.setOnClickListener {
            pickImageFromGallery()
        }

        btn_back_tambah_produk.setOnClickListener {
            val getEtProduk = et_id_produk.text
            Toast.makeText(this, getEtProduk.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            filePath = data!!.data!!

            btn_tambah_photo.setImageURI(data.data) // handle chosen image
            btn_tambah_photo.layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT
            btn_tambah_photo.layoutParams.width = RelativeLayout.LayoutParams.MATCH_PARENT
            btn_tambah_photo.setPadding(0, 0, 0, 0)
            btn_tambah_photo.setBackgroundResource(0)
        }
    }

    private fun getIdProduk() {

        val produkDatabase = mDatabase.child("Produk").orderByKey().limitToLast(1)

        produkDatabase.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(this@TambahProdukActivity, p0.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (data in p0.children) {
                    val idProduk = data.child("id_produk").value.toString()
                    val intIdProduk = idProduk.toInt()

                    newIdProduk = intIdProduk + 1
                    et_id_produk.text =
                        Editable.Factory.getInstance().newEditable(newIdProduk.toString())

                }

            }
        })

    }

    private fun SimpanPhoto() {

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        getUrl = ""
        val ref = storageReference.child("images/" + UUID.randomUUID().toString())
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

                getIdProduk = et_id_produk.text.toString()
                getNmProduk = et_nm_produk.text.toString()
                getStok = et_stok.text.toString()
                getMerk = et_merk_produk.text.toString()
                getDimensi = et_dimensi_produk.text.toString()
                getBerat = et_berat_produk.text.toString()
                getUkuran = et_ukuran_produk.text.toString()
                getKategori = et_kategori_produk.text.toString()
                getPerawatan = et_merk_produk.text.toString()
                getCahaya = et_merk_produk.text.toString()
                getIsi = et_isi.text.toString()
                getTipe = et_tipe_produk.text.toString()
                getJenis = et_jenis.text.toString()
                getHargaBeli = et_harga_beli.text.toString()
                getHargaJual = et_harga_jual.text.toString()
                getDeskripsi = et_deskripsi.text.toString()

                val produk = Produk()
                produk.id_produk = getIdProduk
                produk.nm_produk = getNmProduk
                produk.stok = getStok
                produk.merk = getMerk
                produk.dimensi = getDimensi
                produk.berat = getBerat
                produk.ukuran = getUkuran
                produk.kategori = getKategori
                produk.perawatan = getPerawatan
                produk.cahaya = getCahaya
                produk.isi_produk = getIsi
                produk.tipe_produk = getTipe
                produk.jenis_produk = getJenis
                produk.harga_beli = getHargaBeli
                produk.harga_jasa = getHargaJual
                produk.deskripsi = getDeskripsi
                produk.url = getUrl

                mDatabase.child("Produk").child(getIdProduk).setValue(produk)
                Toast.makeText(this, "Uploaded", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "dapat url gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun pickImageFromGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        val mimeTypes =
            arrayOf("image/jpeg", "image/png")
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(intent, REQUEST_CODE)

    }
}