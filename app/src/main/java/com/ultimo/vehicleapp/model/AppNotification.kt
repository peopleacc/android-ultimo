package com.ultimo.vehicleapp.model

import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val orderId: Int,
    val statusType: String // "pending", "proses", "menunggu_pembayaran", "selesai"
)
