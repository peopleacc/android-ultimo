package com.ultimo.vehicleapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val id: Int,
    val user_id: Int,
    val message: String,
    val created_at: String,
    val subject: String,
)

@Serializable
data class ChatInsert(
    val user_id: Int,
    val subject: String,
    val created_at: String,
    val message: String
)
