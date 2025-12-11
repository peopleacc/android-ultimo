package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.Config.supabase
import com.ultimo.vehicleapp.model.Chat
import com.ultimo.vehicleapp.model.ChatInsert
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns

object ChatRepository {
    suspend fun getChatsForUser(userId: String): List<Chat> {
        return supabase.from("chat")
            .select(
                columns = Columns.list(
                    "id",
                    "user_id",
                    "message",
                    "created_at",
                    "subject"
                )
            ) {
                filter { eq("user_id", userId) }
            }
            .decodeList<Chat>()
    }
}

data class ChatSendResult(val ok: Boolean, val error: String? = null)

object ChatInsertRepository {
    suspend fun insertChat(data: ChatInsert): ChatSendResult {
        return try {
            supabase.from("chat").insert(data)
            ChatSendResult(ok = true)
        } catch (e: Exception) {
            ChatSendResult(ok = false, error = e.message)
        }
    }
}
