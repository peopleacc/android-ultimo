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
                    "pesanan_id", "user_id", "tanggal_pesan", "tanggal_pengerjaan",
                    "estimasi_selesai", "total_estimasi_harga", "status_pengerjaan",
                    "metode_pembayaran", "status_pembayaran", "nominal_pembayaran",
                    "tanggal_pembayaran", "teknisi_id", "product_id", "bahan_id"
                )
            )
            .decodeList<pemesanan>()
    }
}

object PemesananInsertRepository {
    suspend fun insertPemesanan(data: PemesananInsert): Boolean {
        return try {
            // Di Supabase-kt v2, insert() akan melempar exception jika gagal.
            // Kita bungkus dengan try-catch untuk mengembalikan boolean.
            supabase.from("t_pemesanan").insert(data)
            true // Jika tidak ada error, kembalikan true
        } catch (e: Exception) {
            println("Error inserting pemesanan: ${e.message}")
            false // Jika ada error, kembalikan false
        }
    }
}

object PemesananUserRepository {
    suspend fun getAllPemesananUser(userId: String): List<pemesanan> {
        return supabase.from("t_pemesanan")
            .select(
                columns = Columns.list( // Definisikan kolom di sini
                    "pesanan_id", "user_id", "tanggal_pesan", "tanggal_pengerjaan",
                    "estimasi_selesai", "total_estimasi_harga", "status_pengerjaan",
                    "metode_pembayaran", "status_pembayaran", "nominal_pembayaran",
                    "tanggal_pembayaran", "teknisi_id", "product_id", "bahan_id"
                )
            ) { // Filter dan order berada di dalam blok lambda setelah kolom
                filter {
                    eq("user_id", userId) // Sintaks baru untuk filter
                } // Sintaks baru untuk order
            }
            .decodeList<pemesanan>()
    }
}
