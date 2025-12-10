package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.Progress
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order

object ProgressRepository {
    suspend fun getAllProgressPemesananUser(userId: String): List<Progress> {
        return supabase.from("d_progres")
            .select(
                columns = Columns.list(
                    "id",
                    "t_pemesanan(" +
                            "pesanan_id, user_id, tanggal_pesan, tanggal_pengerjaan, estimasi_selesai, " +
                            "total_estimasi_harga, status_pengerjaan, metode_pembayaran, status_pembayaran, " +
                            "nominal_pembayaran, tanggal_pembayaran, teknisi_id, " +
                            "m_product_layanan(product_id, nama_layanan, harga, deskripsi), " +
                            "bahan_id" +
                            ")",
                    "presentase_progress",
                    "keterangan_status"
                )
            ) {
                filter {
                    eq("t_pemesanan.user_id", userId)

                }
                order("id", Order.DESCENDING)
                limit(1)// <-- THIS IS THE FIX                limit(1)
            }// ← LIMIT 1
            .decodeList<Progress>()
    }
}
object AllProgressRepository {
    suspend fun getAllProgressUser(userId: String): List<Progress> {
        return supabase.from("d_progres")
            .select(
                columns = Columns.list(
                    "id",
                    "t_pemesanan(" +
                            "pesanan_id, user_id, tanggal_pesan, tanggal_pengerjaan, estimasi_selesai, " +
                            "total_estimasi_harga, status_pengerjaan, metode_pembayaran, status_pembayaran, " +
                            "nominal_pembayaran, tanggal_pembayaran, teknisi_id, " +
                            "m_product_layanan(product_id, nama_layanan, harga, deskripsi), " +
                            "bahan_id" +
                            ")",
                    "presentase_progress",
                    "keterangan_status"
                )
            ) {
                filter {
                    eq("t_pemesanan.user_id", userId)

                }
                order("id", Order.DESCENDING)

            }// ← LIMIT 1
            .decodeList<Progress>()
    }
}

object DetailProgressRepository {
    suspend fun getDetailProgressUser(userId: String): List<Progress> {
        return supabase.from("d_progres")
            .select(
                columns = Columns.list(
                    "id",
                    "t_pemesanan(" +
                            "pesanan_id, user_id, tanggal_pesan, tanggal_pengerjaan, estimasi_selesai, " +
                            "total_estimasi_harga, status_pengerjaan, metode_pembayaran, status_pembayaran, " +
                            "nominal_pembayaran, tanggal_pembayaran, teknisi_id, " +
                            "m_product_layanan(product_id, nama_layanan, harga, deskripsi), " +
                            "bahan_id" +
                            ")",
                    "presentase_progress",
                    "keterangan_status"
                )
            ) {
                filter {
                    eq("t_pemesanan.user_id", userId)

                }
                order("id", Order.DESCENDING)

            }// ← LIMIT 1
            .decodeList<Progress>()
    }
}

object TotalProsesProgress {
    data class DataProsesProgress(
        val total_proses_progress: Int
    )
}

suspend fun getTotalProsesFromProgress(userId: String): List<TotalProsesProgress.DataProsesProgress> {
    return try {
        // Ambil semua progress user
        val allProgress = AllProgressRepository.getAllProgressUser(userId)
        
        // Filter yang status_pengerjaan = "proses"
        val prosesCount = allProgress.count { progress ->
            progress.t_pemesanan?.status_pengerjaan?.lowercase() == "proses"
        }
        
        listOf(TotalProsesProgress.DataProsesProgress(total_proses_progress = prosesCount))
    } catch (e: Exception) {
        println("Error getting total proses from progress: ${e.message}")
        emptyList()
    }
}
