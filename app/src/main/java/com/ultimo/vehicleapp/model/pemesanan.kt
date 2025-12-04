package com.ultimo.vehicleapp.model


import kotlinx.serialization.Serializable

@Serializable
data class pemesanan(
    val pesanan_id: Int,
    val user_id: Int,
    val tanggal_pesan: String? = null,
    val tanggal_pengerjaan:String? = null,
    val estimasi_selesai: String? = null,
    val total_estimasi_harga: Double,
    val status_pengerjaan: String? = null,
    val metode_pembayaran: String? = null,
    val status_pembayaran: String? = null,
    val nominal_pembayaran: String? = null,
    val tanggal_pembayaran: String? = null,
    val teknisi_id: Int? = null,
    val product_id: Int,
    val bahan_id: Int,
)

@Serializable
data class PemesananInsert(
    val user_id: Int,
    val product_id: Int,
    val bahan_id: Int,
    val total_estimasi_harga: Int,
    val status_pengerjaan: String = "Pending",
    val tanggal_pesan: String,
)


