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

// --- Model untuk Update Personal Information ---
data class UpdatePersonalInfoRequest(
    val token: String,
    val nama: String,
    val email: String,
    val phone: String,
    val address: String?,
    val password: String?  // Simple password field
)

data class UpdatePersonalInfoResponse(
    val status: String,
    val message: String,
    val user: UserData?
)


interface ApiService {
    @POST("api/auth_api")
    fun login(@Body requestBody: LoginRequest): Call<LoginResponse>

    @POST("api/signup_api")
    fun register(@Body requestBody: RegisterRequest): Call<RegisterResponse>

    // 🔹 Ganti GET agar pakai @Query, bukan @Body
    @GET("api/auth_api")
    fun getSession(@Query("token") token: String): Call<SessionResponse>

    // 🔹 API untuk update personal information
    @POST("api/update_personal_info")
    fun updatePersonalInfo(
        @Query("token") token: String,
        @Body requestBody: UpdatePersonalInfoRequest
    ): Call<UpdatePersonalInfoResponse>

    // 🔹 API untuk Forgot Password (single endpoint with action)
    @POST("api/forgot_password")
    fun forgotPassword(@Body requestBody: ForgotPasswordRequest): Call<ForgotPasswordResponse>
}

// --- Forgot Password ---
data class ForgotPasswordRequest(
    val action: String,          // "send_otp", "verify_otp", "reset_password"
    val email: String,
    val otp: String? = null,
    val token: String? = null,
    val new_password: String? = null
)

data class ForgotPasswordResponse(
    val status: String,
    val message: String,
    val token: String? = null    // Token untuk reset password (dari verify_otp)
)

