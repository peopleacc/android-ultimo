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
        // Load session dari local storage saat aplikasi dibuka (hanya sekali)
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    isInitialized = true
                    viewModelScope.launch {
                        try {
                            // Load token dari local storage (selalu load, tidak peduli remember me)
                            val savedToken = sessionManager.getSessionToken().first()
                            if (!savedToken.isNullOrEmpty()) {
                                // Set token dulu agar MainActivity bisa langsung navigate ke Home
                                _token.value = savedToken
                                // Set loading ke false dulu agar UI bisa render
                                _isLoading.value = false
                                // Fetch user data di background (tidak blocking)
                                fetchUserData(savedToken)
                            } else {
                                _token.value = null
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

    // Simpan token login ke local storage (selalu simpan, tidak peduli remember me)
    fun saveSessionToken(token: String, rememberMe: Boolean = true) {
        viewModelScope.launch {
            // Selalu simpan token ke local storage agar persist saat aplikasi ditutup
            sessionManager.saveSessionToken(token)
            _token.value = token
            // Simpan remember me preference juga
            sessionManager.saveRememberMe(rememberMe)
            // Set loading ke false setelah save token (jika user data sudah ada dari login)
            if (_user.value != null) {
                _isLoading.value = false
            }
        }
    }

    // Simpan remember me preference (hanya untuk UI, tidak mempengaruhi penyimpanan token)
    fun saveRememberMe(remember: Boolean) {
        viewModelScope.launch {
            sessionManager.saveRememberMe(remember)
            // Token tetap tersimpan di local storage terlepas dari remember me
            // Remember me hanya untuk preferensi UI
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
    // Dipanggil di background setelah token di-set, tidak blocking UI
    private fun fetchUserData(token: String) {
        // Jangan set loading ke true karena ini background task
        // UI sudah di-render dengan token yang ada
        ApiClient.instance.getSession(token).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    _user.value = response.body()?.user
                } else {
                    // Jika response tidak successful, cek apakah benar-benar token invalid
                    val errorCode = response.code()
                    if (errorCode == 401 || errorCode == 403) {
                        // Hanya clear jika benar-benar unauthorized (token invalid/expired)
                        viewModelScope.launch {
                            sessionManager.clearAll()
                            _token.value = null
                            _user.value = null
                        }
                    } else {
                        // Untuk error lain (500, network timeout, dll), biarkan token tetap ada
                        // User bisa tetap menggunakan aplikasi dengan token yang ada
                        // User data akan di-fetch lagi nanti
                        _user.value = null
                    }
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                // Jika network error, jangan clear token - mungkin hanya masalah koneksi
                // Biarkan token tetap ada agar user bisa tetap menggunakan aplikasi
                // User data akan di-fetch lagi saat ada koneksi
                _user.value = null
            }
        })
    }

    // Logout - hapus semua session dari local storage
    fun logout() {
        viewModelScope.launch {
            // Selalu hapus semua session saat logout (token dan remember me)
            sessionManager.clearAll()
            _token.value = null
            _user.value = null
        }
    }
}
