package com.ultimo.vehicleapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ultimo.vehicleapp.MainActivity
import com.ultimo.vehicleapp.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "order_status_channel"
        const val CHANNEL_NAME = "Order Status Updates"
        const val CHANNEL_DESCRIPTION = "Notifications for order status changes"
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showOrderStatusNotification(
        orderId: Int,
        title: String,
        message: String,
        notificationId: Int = orderId
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("orderId", orderId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            orderId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // User denied notification permission
            e.printStackTrace()
        }
    }

    fun getStatusTitle(status: String): String {
        return when (status.lowercase()) {
            "pending" -> "Pesanan Diterima"
            "proses" -> "Pesanan Sedang Diproses"
            "menunggu pembayaran" -> "Menunggu Pembayaran"
            "selesai" -> "Pesanan Selesai"
            else -> "Update Pesanan"
        }
    }

    fun getStatusMessage(status: String, orderId: Int): String {
        return when (status.lowercase()) {
            "pending" -> "Pesanan #$orderId telah diterima dan menunggu konfirmasi."
            "proses" -> "Pesanan #$orderId sedang dalam pengerjaan."
            "menunggu pembayaran" -> "Pesanan #$orderId menunggu pembayaran Anda."
            "selesai" -> "Pesanan #$orderId telah selesai! Terima kasih."
            else -> "Status pesanan #$orderId telah diperbarui."
        }
    }
}
