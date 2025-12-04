package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.pemesanan
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns


object PemesananRepository {

    suspend fun getAllPemesanan(): List<pemesanan> {
        return supabase.from("t_pemesanan")
            .select(
                Columns.list(
                    "pesanan_id",
                    "user_id",
                    "tanggal_pesan",
                    "tanggal_pengerjaan",
                    "estimasi_selesai",
                    "total_estimasi_harga",
                    "status_pengerjaan",
                    "metode_pembayaran",
                    "status_pembayaran",
                    "nominal_pembayaran",
                    "tanggal_pembayaran",
                    "teknisi_id",
                    "product_id",
                    "bahan_id"
                )
            )
            .decodeList<pemesanan>()
    }
}

object PemesananInsertRepository {
    suspend fun insertPemesanan(data: PemesananInsert): Boolean {
        val response = supabase.from("t_pemesanan")
            .insert(data)

        return response != null
    }
}
