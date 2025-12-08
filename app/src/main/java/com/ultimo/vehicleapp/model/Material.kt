package com.ultimo.vehicleapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Material_List(
    val bahan_id: Int,
    val nama_bahan: String,
    val deskripsi: String?,
    val harga_per_unit: Int,
)
