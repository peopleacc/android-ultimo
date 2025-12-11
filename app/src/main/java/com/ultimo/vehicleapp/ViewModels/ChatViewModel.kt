package com.ultimo.vehicleapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.ChatInsertRepository
import com.ultimo.vehicleapp.Controller.ChatSendResult
import com.ultimo.vehicleapp.Controller.ChatRepository
import com.ultimo.vehicleapp.model.Chat
import com.ultimo.vehicleapp.model.ChatInsert
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    fun loadChatsForUser(userId: Int) {
        viewModelScope.launch {
            _chats.value = ChatRepository.getChatsForUser(userId.toString()).reversed()
        }
    }

    suspend fun sendMessage(userId: Int, subject: String, message: String): ChatSendResult {
        val now = java.time.Instant.now().toString()
        val data = ChatInsert(
            user_id = userId,
            subject = subject,
            message = message,
            created_at = now
        )
        val result = ChatInsertRepository.insertChat(data)
        if (result.ok) {
            loadChatsForUser(userId)
        }
        return result
    }
}
