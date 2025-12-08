package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.pemesanan
import com.ultimo.vehicleapp.model.t_pemesanan
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count

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

object PemesananDeleteRepository {

    suspend fun deletePending(): Boolean {
        return try {
            supabase.from("t_pemesanan")
                .delete {
                    filter {
                        eq("status_pengerjaan", "pending")                     }
                }
                   // wajib, agar delete dieksekusi

            true
        } catch (e: Exception) {
            println("Error deleting pemesanan: ${e.message}")
            false
        }
    }

    suspend fun deletePemesananById(pesananId: Int): Boolean {
        return try {
            supabase.from("t_pemesanan")
                .delete {
                    filter {
                        eq("pesanan_id", pesananId)
                    }
                }
                  // wajib, agar delete dieksekusi
            true
        } catch (e: Exception) {
            println("Error deleting pemesanan by id: ${e.message}")
            false
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

object TotalSelesai {
    data class DataSelesai(
        val total_selesai: Int
    )
}
object TotalProses {
    data class DataProses(
        val total_proses: Int
    )
}
suspend fun getTotalSelesai(userId: String): List<TotalSelesai.DataSelesai> {
    return try {
        // Menggunakan select(head=true) dan count() adalah cara paling efisien
        // untuk mendapatkan jumlah baris tanpa mengambil datanya.
        val response = supabase.from("t_pemesanan")
            .select(head = true) {
                filter {
                    eq("user_id", userId)
                    eq("status_pengerjaan", "selesai")
                }
                count(Count.EXACT)
            }

        // Jumlah data (count) tersedia di dalam response
        val totalCount = response.countOrNull()?.toInt() ?: 0

        // Fungsi ini mengharapkan List<DataSelesai>, jadi kita bungkus hasilnya.
        listOf(TotalSelesai.DataSelesai(total_selesai = totalCount))
    } catch (e: Exception) {
        println("Error getting total selesai: ${'$'}{e.message}")
        emptyList() // Kembalikan list kosong jika terjadi error
    }
}

suspend fun getTotalProses(userId: String): List<TotalSelesai.DataSelesai> {
    return try {
        // Menggunakan select(head=true) dan count() adalah cara paling efisien
        // untuk mendapatkan jumlah baris tanpa mengambil datanya.
        val response = supabase.from("t_pemesanan")
            .select(head = true) {
                filter {
                    eq("user_id", userId)
                    eq("status_pengerjaan", "selesai")
                }
                count(Count.EXACT)
            }

        // Jumlah data (count) tersedia di dalam response
        val totalCount = response.countOrNull()?.toInt() ?: 0

        // Fungsi ini mengharapkan List<DataSelesai>, jadi kita bungkus hasilnya.
        listOf(TotalSelesai.DataSelesai(total_selesai = totalCount))
    } catch (e: Exception) {
        println("Error getting total selesai: ${'$'}{e.message}")
        emptyList() // Kembalikan list kosong jika terjadi error
    }
}
