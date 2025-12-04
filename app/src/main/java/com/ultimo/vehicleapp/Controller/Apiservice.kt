package com.ultimo.vehicleapp.Controller

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// --- Model untuk Login ---
data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
    val session: String?, // Token session dari server
    val user: UserData?
)

// --- Model untuk Register ---
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterResponse(
    val status: String,
    val message: String
)

// --- Model untuk User Session ---
data class SessionRequest(
    val token: String
)

data class UserData(
    val id: Int?,
    val nama: String?,
    val email: String?,
    val phone: String?,
    val address : String?
)

data class SessionResponse(
    val status: String,
    val message: String?,
    val user: UserData?
)

// --- Design DTO untuk API ---


// --- Interface Retrofit ---
interface ApiService {
    @POST("api/auth_api")
    fun login(@Body requestBody: LoginRequest): Call<LoginResponse>

    @POST("api/signup_api")
    fun register(@Body requestBody: RegisterRequest): Call<RegisterResponse>

    // 🔹 Ganti GET agar pakai @Query, bukan @Body
    @GET("api/auth_api")
    fun getSession(@Query("token") token: String): Call<SessionResponse>

}

