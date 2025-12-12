package com.ultimo.vehicleapp.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    @SerializedName("notif_id")
    @SerialName("notif_id")
    val id: Int = 0,
    
    @SerializedName("user_id")
    @SerialName("user_id")
    val userId: Int,
    
    @SerializedName("pesanan_id")
    @SerialName("pesanan_id")
    val pesananId: Int,
    
    @SerializedName("tipe_notif")
    @SerialName("tipe_notif")
    val tipeNotif: String,
    
    @SerializedName("pesan")
    @SerialName("pesan")
    val pesan: String,
    
    @SerializedName("sudah_dibaca")
    @SerialName("sudah_dibaca")
    val sudahDibaca: Boolean = false,
    
    @SerializedName("timestamp")
    @SerialName("timestamp")
    val timestamp: String? = null
) {
    // Helper properties for backward compatibility
    val title: String
        get() = when (tipeNotif.lowercase()) {
            "pending" -> "Pesanan Menunggu Konfirmasi"
            "proses" -> "Pesanan Sedang Dikerjakan"
            "menunggu_pembayaran" -> "Menunggu Pembayaran"
            "selesai" -> "Pesanan Selesai"
            else -> "Update Pesanan"
        }
    
    val message: String
        get() = pesan
    
    val isRead: Boolean
        get() = sudahDibaca
    
    val orderId: Int
        get() = pesananId
    
    val statusType: String
        get() = tipeNotif
    
    // Convert timestamp string to millis for display
    val timestampMillis: Long
        get() = try {
            // Parse ISO timestamp or return current time
            timestamp?.let {
                java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                    .parse(it)?.time ?: System.currentTimeMillis()
            } ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
}
