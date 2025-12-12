package com.ultimo.vehicleapp.Controller

import com.ultimo.vehicleapp.model.AppNotification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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
    val address : String?,
    val foto_profile: String? = null
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
    val password: String?,
    val foto_profile: String? = null
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

    // 🔹 API untuk update personal information (tanpa password)
    @POST("api/update_personal_info")
    fun updatePersonalInfo(
        @Query("token") token: String,
        @Body requestBody: UpdatePersonalInfoRequest
    ): Call<UpdatePersonalInfoResponse>

    // 🔹 API untuk Change Password (terpisah)
    @POST("api/change_password")
    fun changePassword(
        @Query("token") token: String,
        @Body requestBody: ChangePasswordRequest
    ): Call<ChangePasswordResponse>

    // 🔹 API untuk Forgot Password (single endpoint with action)
    @POST("api/forgot_password")
    fun forgotPassword(@Body requestBody: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // 🔹 API untuk Notifications
    @GET("api/notifications")
    fun getNotifications(@Query("user_id") userId: Int): Call<NotificationResponse>

    @POST("api/notifications")
    fun createNotification(@Body requestBody: CreateNotificationRequest): Call<CreateNotificationResponse>

    @PATCH("api/notifications")
    fun markNotificationAsRead(@Body requestBody: MarkReadRequest): Call<MarkReadResponse>
}

// --- Change Password ---
data class ChangePasswordRequest(
    val token: String,
    val current_password: String,
    val new_password: String
)

data class ChangePasswordResponse(
    val status: String,
    val message: String
)

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

// --- Notification ---
data class NotificationResponse(
    val status: String,
    val message: String,
    val data: List<AppNotification>?
)

data class CreateNotificationRequest(
    val user_id: Int,
    val pesanan_id: Int,
    val tipe_notif: String,
    val pesan: String
)

data class CreateNotificationResponse(
    val status: String,
    val message: String,
    val data: AppNotification?
)

data class MarkReadRequest(
    val notif_id: Int? = null,
    val user_id: Int? = null,
    val mark_all: Boolean? = null
)

data class MarkReadResponse(
    val status: String,
    val message: String,
    val data: Any?
)


