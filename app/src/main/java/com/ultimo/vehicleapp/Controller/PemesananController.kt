package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.pemesanan
import com.ultimo.vehicleapp.model.t_pemesanan
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.Serializable

@Serializable
data class BuktiPembayaranUpdate(
    val bukti_pembayaran: String
)

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

object TotalPending {
    data class DataPending(
        val total_pending: Int
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

suspend fun getTotalProses(userId: String): List<TotalProses.DataProses> {
    return try {
        // Menggunakan select(head=true) dan count() adalah cara paling efisien
        // untuk mendapatkan jumlah baris tanpa mengambil datanya.
        val response = supabase.from("t_pemesanan")
            .select(head = true) {
                filter {
                    eq("user_id", userId)
                    eq("status_pengerjaan", "proses")
                }
                count(Count.EXACT)
            }

        // Jumlah data (count) tersedia di dalam response
        val totalCount = response.countOrNull()?.toInt() ?: 0

        // Fungsi ini mengharapkan List<DataProses>, jadi kita bungkus hasilnya.
        listOf(TotalProses.DataProses(total_proses = totalCount))
    } catch (e: Exception) {
        println("Error getting total proses: ${e.message}")
        emptyList() // Kembalikan list kosong jika terjadi error
    }
}

suspend fun getTotalPending(userId: String): List<TotalPending.DataPending> {
    return try {
        // Mengambil jumlah pesanan dengan status pending
        val responsePending = supabase.from("t_pemesanan")
            .select(head = true) {
                filter {
                    eq("user_id", userId)
                    eq("status_pengerjaan", "pending")
                }
                count(Count.EXACT)
            }
        val pendingCount = responsePending.countOrNull()?.toInt() ?: 0

        // Mengambil jumlah pesanan dengan status waiting for order
        val responseWaiting = supabase.from("t_pemesanan")
            .select(head = true) {
                filter {
                    eq("user_id", userId)
                    eq("status_pengerjaan", "waiting for order")
                }
                count(Count.EXACT)
            }
        val waitingCount = responseWaiting.countOrNull()?.toInt() ?: 0

        // Total pending + waiting for order
        val totalCount = pendingCount + waitingCount
        listOf(TotalPending.DataPending(total_pending = totalCount))
    } catch (e: Exception) {
        println("Error getting total pending: ${e.message}")
        emptyList()
    }
}

/**
 * Repository untuk mengelola bukti pembayaran
 */
object BuktiPembayaranRepository {
    /**
     * Upload image ke Supabase Storage bucket "gambar"
     * @param pesananId ID pesanan untuk nama file
     * @param imageBytes Data gambar dalam bentuk ByteArray
     * @param fileExtension Ekstensi file (contoh: "jpg", "png")
     * @return URL publik dari file yang diupload, atau null jika gagal
     */
    suspend fun uploadBuktiPembayaran(
        pesananId: Int,
        imageBytes: ByteArray,
        fileExtension: String = "jpg"
    ): String? {
        return try {
            val bucket = supabase.storage.from("gambar")
            val timestamp = System.currentTimeMillis()
            val path = "bukti_pembayaran/${pesananId}_${timestamp}.$fileExtension"
            
            bucket.upload(path, imageBytes, upsert = true)
            bucket.publicUrl(path)
        } catch (e: Exception) {
            println("Error uploading bukti pembayaran: ${e.message}")
            null
        }
    }
    
    /**
     * Update kolom bukti_pembayaran di t_pemesanan
     * Status pesanan TIDAK diubah
     * @param pesananId ID pesanan yang akan diupdate
     * @param imageUrl URL gambar bukti pembayaran
     * @return true jika berhasil, false jika gagal
     */
    suspend fun updateBuktiPembayaran(
        pesananId: Int,
        imageUrl: String
    ): Boolean {
        return try {
            supabase.from("t_pemesanan")
                .update(BuktiPembayaranUpdate(bukti_pembayaran = imageUrl)) {
                    filter { eq("pesanan_id", pesananId) }
                }
            true
        } catch (e: Exception) {
            println("Error updating bukti pembayaran: ${e.message}")
            false
        }
    }
}

@Serializable
data class UploadGambarUpdate(
    val upload_gambar: String
)

/**
 * Repository untuk mengelola upload gambar bukti pembayaran
 */
object UploadGambarRepository {
    /**
     * Upload image ke Supabase Storage bucket "gambar"
     * @param pesananId ID pesanan untuk nama file
     * @param imageBytes Data gambar dalam bentuk ByteArray
     * @param fileExtension Ekstensi file (contoh: "jpg", "png")
     * @return URL publik dari file yang diupload, atau null jika gagal
     */
    suspend fun uploadGambar(
        pesananId: Int,
        imageBytes: ByteArray,
        fileExtension: String = "jpg"
    ): String? {
        return try {
            val bucket = supabase.storage.from("gambar")
            val timestamp = System.currentTimeMillis()
            val path = "upload_gambar/${pesananId}_${timestamp}.$fileExtension"
            
            bucket.upload(path, imageBytes, upsert = true)
            bucket.publicUrl(path)
        } catch (e: Exception) {
            println("Error uploading gambar: ${e.message}")
            null
        }
    }
    
    /**
     * Update kolom upload_gambar di t_pemesanan
     * Status pembayaran TIDAK diubah (tetap "menunggu pembayaran")
     * @param pesananId ID pesanan yang akan diupdate
     * @param imageUrl URL gambar bukti pembayaran
     * @return true jika berhasil, false jika gagal
     */
    suspend fun updateUploadGambar(
        pesananId: Int,
        imageUrl: String
    ): Boolean {
        return try {
            supabase.from("t_pemesanan")
                .update(UploadGambarUpdate(upload_gambar = imageUrl)) {
                    filter { eq("pesanan_id", pesananId) }
                }
            true
        } catch (e: Exception) {
            println("Error updating upload_gambar: ${e.message}")
            false
        }
    }
}
