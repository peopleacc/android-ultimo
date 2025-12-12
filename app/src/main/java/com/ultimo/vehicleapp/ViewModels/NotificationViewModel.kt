package com.ultimo.vehicleapp.ViewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.Controller.CreateNotificationRequest
import com.ultimo.vehicleapp.Controller.CreateNotificationResponse
import com.ultimo.vehicleapp.Controller.MarkReadRequest
import com.ultimo.vehicleapp.Controller.MarkReadResponse
import com.ultimo.vehicleapp.Controller.NotificationResponse
import com.ultimo.vehicleapp.model.AppNotification
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationHelper = NotificationHelper(application)
    private val apiService = ApiClient.instance

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

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Current user ID for filtering
    private var currentUserId: Int? = null

    // Track subscribed status
    private var isSubscribed = false

    companion object {
        private const val TAG = "NotificationViewModel"
    }

    /**
     * Fetch notifications from database via API
     */
    fun fetchNotificationsFromDatabase(userId: Int) {
        _isLoading.value = true
        
        apiService.getNotifications(userId).enqueue(object : Callback<NotificationResponse> {
            override fun onResponse(
                call: Call<NotificationResponse>,
                response: Response<NotificationResponse>
            ) {
                _isLoading.value = false
                
                if (response.isSuccessful) {
                    val notificationResponse = response.body()
                    if (notificationResponse?.status == "success") {
                        _notifications.value = notificationResponse.data ?: emptyList()
                        updateUnreadCount()
                        Log.d(TAG, "Fetched ${notificationResponse.data?.size ?: 0} notifications")
                    } else {
                        Log.e(TAG, "Failed to fetch notifications: ${notificationResponse?.message}")
                    }
                } else {
                    Log.e(TAG, "API error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<NotificationResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Network error fetching notifications: ${t.message}", t)
            }
        })
    }

    /**
     * Start listening to realtime updates for a specific user
     */
    fun subscribeToOrderUpdates(userId: Int) {
        if (isSubscribed && currentUserId == userId) return
        
        currentUserId = userId
        isSubscribed = true

        // Fetch existing notifications from database
        fetchNotificationsFromDatabase(userId)

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

            // Save to database via API and add to local list
            createNotificationInDatabase(
                userId = userId,
                pesananId = pesananId,
                tipeNotif = newStatus.lowercase().replace(" ", "_"),
                pesan = message
            )

        } catch (e: Exception) {
            Log.e(TAG, "Error handling order update: ${e.message}", e)
        }
    }

    /**
     * Create notification in database via API
     */
    private fun createNotificationInDatabase(
        userId: Int,
        pesananId: Int,
        tipeNotif: String,
        pesan: String
    ) {
        val request = CreateNotificationRequest(
            user_id = userId,
            pesanan_id = pesananId,
            tipe_notif = tipeNotif,
            pesan = pesan
        )

        apiService.createNotification(request).enqueue(object : Callback<CreateNotificationResponse> {
            override fun onResponse(
                call: Call<CreateNotificationResponse>,
                response: Response<CreateNotificationResponse>
            ) {
                if (response.isSuccessful) {
                    val createResponse = response.body()
                    if (createResponse?.status == "success" && createResponse.data != null) {
                        // Add to local list
                        _notifications.value = listOf(createResponse.data) + _notifications.value
                        updateUnreadCount()
                        
                        // Trigger popup
                        _latestNotification.value = createResponse.data
                        _showPopup.value = true
                        
                        Log.d(TAG, "Notification created successfully in database")
                    } else {
                        Log.e(TAG, "Failed to create notification: ${createResponse?.message}")
                    }
                } else {
                    Log.e(TAG, "API error creating notification: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<CreateNotificationResponse>, t: Throwable) {
                Log.e(TAG, "Network error creating notification: ${t.message}", t)
            }
        })
    }

    fun dismissPopup() {
        _showPopup.value = false
    }

    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.sudahDibaca }
    }

    /**
     * Mark single notification as read
     */
    fun markAsRead(notificationId: Int) {
        val request = MarkReadRequest(notif_id = notificationId)

        apiService.markNotificationAsRead(request).enqueue(object : Callback<MarkReadResponse> {
            override fun onResponse(
                call: Call<MarkReadResponse>,
                response: Response<MarkReadResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    // Update local state
                    _notifications.value = _notifications.value.map { notification ->
                        if (notification.id == notificationId) {
                            notification.copy(sudahDibaca = true)
                        } else {
                            notification
                        }
                    }
                    updateUnreadCount()
                    Log.d(TAG, "Notification marked as read")
                } else {
                    Log.e(TAG, "Failed to mark notification as read")
                }
            }

            override fun onFailure(call: Call<MarkReadResponse>, t: Throwable) {
                Log.e(TAG, "Network error marking as read: ${t.message}", t)
            }
        })
    }

    /**
     * Mark all notifications as read for current user
     */
    fun markAllAsRead() {
        currentUserId?.let { userId ->
            val request = MarkReadRequest(user_id = userId, mark_all = true)

            apiService.markNotificationAsRead(request).enqueue(object : Callback<MarkReadResponse> {
                override fun onResponse(
                    call: Call<MarkReadResponse>,
                    response: Response<MarkReadResponse>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        // Update local state
                        _notifications.value = _notifications.value.map { it.copy(sudahDibaca = true) }
                        updateUnreadCount()
                        Log.d(TAG, "All notifications marked as read")
                    } else {
                        Log.e(TAG, "Failed to mark all as read")
                    }
                }

                override fun onFailure(call: Call<MarkReadResponse>, t: Throwable) {
                    Log.e(TAG, "Network error marking all as read: ${t.message}", t)
                }
            })
        }
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
