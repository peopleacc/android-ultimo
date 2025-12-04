package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.Material_List
import com.ultimo.vehicleapp.model.ProductLayanan
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

object MaterialRepository {

    suspend fun getAllMaterial(): List<Material_List> {
        return supabase.from("m_bahan")
            .select(
                Columns.list(
                    "bahan_id",
                    "nama_bahan",
                    "deskripsi",
                    "harga_per_unit",
                ))
            .decodeList<Material_List>()
    }

}