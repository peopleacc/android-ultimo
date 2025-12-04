package com.ultimo.vehicleapp.model
import kotlinx.serialization.Serializable

@Serializable
data class ProductLayanan(
    val product_id: Int,
    val nama_layanan: String,
    val jenis_kategori: String,
    val harga: Double,
    val deskripsi: String?
)
