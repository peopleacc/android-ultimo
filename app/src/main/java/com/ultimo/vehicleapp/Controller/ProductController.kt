package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.ProductLayanan
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

object ProductRepository {

    suspend fun getAllProducts(): List<ProductLayanan> {
        return supabase.from("m_product_layanan")
            .select(
                Columns.list(
                "product_id",
                "nama_layanan",
                "gambar_url",
                "harga",
                "deskripsi"
            ))
            .decodeList<ProductLayanan>()
    }

}