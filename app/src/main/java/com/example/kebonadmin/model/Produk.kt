package com.example.kebonadmin.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Produk(

    var berat: String? = "",
    var cahaya: String? = "",
    var deskripsi: String? = "",
    var dimensi: String? = "",
    var harga_beli: String? = "",
    var harga_jasa: String? = "",
    var id_produk: String? = "",
    var isi_produk: String? = "",
    var jenis_produk: String? = "",
    var kategori: String? = "",
    var merk: String? = "",
    var nm_produk: String? = "",
    var perawatan: String? = "",
    var stok: String? = "",
    var tipe_produk: String? = "",
    var ukuran: String? = "",
    var url: String? = ""

) : Parcelable