package com.ultimo.vehicleapp.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.AppNotification
import com.ultimo.vehicleapp.model.pemesanan
import com.ultimo.vehicleapp.notification.NotificationHelper
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.UUID

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)

    // In-app notifications list
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications

    // Unread count
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    // Popup notification state
    private val _showPopup = MutableStateFlow(false)
    val showPopup: StateFlow<Boolean> = _showPopup

    // Latest notification for popup display
    private val _latestNotification = MutableStateFlow<AppNotification?>(null)
    val latestNotification: StateFlow<AppNotification?> = _latestNotification

    // Current user ID for filtering
    private var currentUserId: Int? = null

    // Track subscribed status
    private var isSubscribed = false

    companion object {
        private const val TAG = "NotificationViewModel"
    }

    /**
     * Start listening to realtime updates for a specific user
     */
    fun subscribeToOrderUpdates(userId: Int) {
        if (isSubscribed && currentUserId == userId) return
        
        currentUserId = userId
        isSubscribed = true

        viewModelScope.launch {
            try {
                // Create a channel for the t_pemesanan table
                val channel = supabase.realtime.channel("order_updates_$userId")

                // Listen for UPDATE events on t_pemesanan table
                val changeFlow = channel.postgresChangeFlow<PostgresAction.Update>(schema = "public") {
                    table = "t_pemesanan"
                    filter = "user_id=eq.$userId"
                }

                // Process changes
                changeFlow.onEach { change ->
                    handleOrderUpdate(change)
                }.launchIn(viewModelScope)

                // Subscribe to the channel
                channel.subscribe()
                
                Log.d(TAG, "Subscribed to order updates for user $userId")

            } catch (e: Exception) {
                Log.e(TAG, "Error subscribing to realtime: ${e.message}", e)
            }
        }
    }

    private fun handleOrderUpdate(change: PostgresAction.Update) {
        try {
            val record = change.record
            
            val pesananId = record["pesanan_id"]?.jsonPrimitive?.content?.toIntOrNull() ?: return
            val newStatus = record["status_pengerjaan"]?.jsonPrimitive?.content ?: return
            val userId = record["user_id"]?.jsonPrimitive?.content?.toIntOrNull() ?: return

            // Only process if it's for current user
            if (userId != currentUserId) return

            Log.d(TAG, "Order $pesananId status changed to: $newStatus")

            // Create notification
            val title = notificationHelper.getStatusTitle(newStatus)
            val message = notificationHelper.getStatusMessage(newStatus, pesananId)

            // Show push notification
            notificationHelper.showOrderStatusNotification(
                orderId = pesananId,
                title = title,
                message = message
            )

            // Add to in-app notifications
            addNotification(
                orderId = pesananId,
                title = title,
                message = message,
                statusType = newStatus.lowercase().replace(" ", "_")
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error handling order update: ${e.message}", e)
        }
    }

    private fun addNotification(orderId: Int, title: String, message: String, statusType: String) {
        val notification = AppNotification(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            orderId = orderId,
            statusType = statusType
        )

        _notifications.value = listOf(notification) + _notifications.value
        updateUnreadCount()
        
        // Trigger popup
        _latestNotification.value = notification
        _showPopup.value = true
    }

    fun dismissPopup() {
        _showPopup.value = false
    }

    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }

    fun markAsRead(notificationId: String) {
        _notifications.value = _notifications.value.map { notification ->
            if (notification.id == notificationId) {
                notification.copy(isRead = true)
            } else {
                notification
            }
        }
        updateUnreadCount()
    }

    fun markAllAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
        updateUnreadCount()
    }

    fun clearNotifications() {
        _notifications.value = emptyList()
        updateUnreadCount()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                supabase.realtime.removeAllChannels()
            } catch (e: Exception) {
                Log.e(TAG, "Error removing channels: ${e.message}")
            }
        }
    }
}
