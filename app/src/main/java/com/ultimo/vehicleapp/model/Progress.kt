package com.ultimo.vehicleapp.model
import kotlinx.serialization.Serializable

@Serializable
data class Progress(
    val id: Int,
    val pesanan_id: Int? = null,
    val t_pemesanan: t_pemesanan? = null,
    val presentase_progress: Int,
    val keterangan_status: String? = null
)


@Serializable
data class t_pemesanan(
    val pesanan_id: Int,
    val user_id: Int,
    val tanggal_pesan: String?,
    val tanggal_pengerjaan: String?,
    val estimasi_selesai: String?,
    val total_estimasi_harga: Int,
    val status_pengerjaan: String?,
    val metode_pembayaran: String? = null,
    val status_pembayaran: String? = null,
    val nominal_pembayaran: Double? = null,
    val tanggal_pembayaran: String? = null,
    val teknisi_id: Int? = null,
    val m_product_layanan: ProductLayanan? = null,
    val bahan_id: Int? = null,
)





