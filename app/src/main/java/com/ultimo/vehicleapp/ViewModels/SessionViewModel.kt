package com.ultimo.vehicleapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Controller.SessionResponse
import com.ultimo.vehicleapp.Controller.UserData
import com.ultimo.vehicleapp.data.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application.applicationContext)

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> get() = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading
    
    @Volatile
    private var isInitialized = false

    init {
        // Load session saat aplikasi dibuka (hanya sekali)
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    isInitialized = true
                    viewModelScope.launch {
                        try {
                            val savedToken = sessionManager.getSessionToken().first()
                            _token.value = savedToken
                            if (!savedToken.isNullOrEmpty()) {
                                fetchUserData(savedToken)
                            } else {
                                _isLoading.value = false
                            }
                        } catch (e: Exception) {
                            // Handle error gracefully
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    // Simpan token login
    fun saveSessionToken(token: String) {
        viewModelScope.launch {
            sessionManager.saveSessionToken(token)
            _token.value = token
            // Set loading ke false setelah save token (jika user data sudah ada dari login)
            if (_user.value != null) {
                _isLoading.value = false
            }
        }
    }

    // Simpan remember me preference
    fun saveRememberMe(remember: Boolean) {
        viewModelScope.launch {
            sessionManager.saveRememberMe(remember)
        }
    }

    // Set user data langsung (dari LoginResponse)
    fun setUserData(userData: UserData?) {
        _user.value = userData
        // Set loading ke false setelah user data di-set
        _isLoading.value = false
    }

    // 🔥 Fungsi baru — panggil setelah login berhasil
    fun fetchUserDataAfterLogin(token: String) {
        _isLoading.value = true
        ApiClient.instance.getSession(token).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.status == "success") {
                    _user.value = response.body()?.user
                } else {
                    // Jika response tidak berhasil, set user ke null
                    _user.value = null
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                _isLoading.value = false
                _user.value = null
                // Error handling - bisa ditambahkan logging atau error state
            }
        })
    }

    // Ambil user dari session token (saat startup)
    private fun fetchUserData(token: String) {
        _isLoading.value = true
        ApiClient.instance.getSession(token).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.status == "success") {
                    _user.value = response.body()?.user
                } else {
                    // Jika token invalid, clear session tapi jangan langsung logout
                    // Biarkan MainActivity yang handle navigasi berdasarkan token
                    viewModelScope.launch {
                        sessionManager.clearAll()
                        _token.value = null
                        _user.value = null
                    }
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                _isLoading.value = false
                // Jika gagal, jangan langsung logout - mungkin network error
                // Hanya clear jika memang token tidak valid
                // Biarkan user tetap bisa menggunakan aplikasi jika token masih ada
            }
        })
    }

    // Logout
    fun logout() {
        viewModelScope.launch {
            val rememberMe = sessionManager.getRememberMe().first()
            // Jika remember me tidak dicentang, hapus semua session
            if (!rememberMe) {
                sessionManager.clearAll()
                _token.value = null
                _user.value = null
            } else {
                // Jika remember me dicentang, hanya clear state tapi token tetap tersimpan
                _token.value = null
                _user.value = null
            }
        }
    }
}
