package com.ultimo.vehicleapp.ViewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Config.ApiClient
import com.ultimo.vehicleapp.Controller.SessionResponse
import com.ultimo.vehicleapp.Controller.UpdatePersonalInfoRequest
import com.ultimo.vehicleapp.Controller.UpdatePersonalInfoResponse
import com.ultimo.vehicleapp.Controller.UserData
import com.ultimo.vehicleapp.data.UserDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SessionViewModel(application: Application) : AndroidViewModel(application) {

    // SQLite Database Helper
    private val dbHelper = UserDatabaseHelper(application.applicationContext)

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> get() = _token

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> get() = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> get() = _rememberMe
    
    @Volatile
    private var isInitialized = false

    init {
        // Load session dari SQLite saat aplikasi dibuka (hanya sekali)
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    isInitialized = true
                    viewModelScope.launch {
                        try {
                            loadUserFromDatabase()
                        } catch (e: Exception) {
                            _isLoading.value = false
                        }
                    }
                }
            }
        }
    }

    /**
     * Load user data dari SQLite database
     */
    private suspend fun loadUserFromDatabase() {
        withContext(Dispatchers.IO) {
            // Cek apakah ada user tersimpan dengan Remember Me aktif
            val userSession = dbHelper.getUser()
            
            withContext(Dispatchers.Main) {
                if (userSession != null && userSession.rememberMe) {
                    // Ada user tersimpan dengan Remember Me aktif
                    _token.value = userSession.token
                    _rememberMe.value = userSession.rememberMe
                    _user.value = UserData(
                        id = userSession.userId,
                        nama = userSession.nama,
                        email = userSession.email,
                        phone = userSession.phone,
                        address = userSession.address
                    )
                    _isLoading.value = false
                    
                    // Fetch fresh user data di background (optional)
                    userSession.token?.let { fetchUserData(it) }
                } else {
                    // Tidak ada user tersimpan atau Remember Me tidak aktif
                    // Clear database untuk memastikan
                    withContext(Dispatchers.IO) {
                        dbHelper.clearUser()
                    }
                    _token.value = null
                    _user.value = null
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * Simpan session token dan user data ke SQLite jika Remember Me aktif
     */
    fun saveSessionToken(token: String, rememberMe: Boolean = true) {
        viewModelScope.launch {
            _token.value = token
            _rememberMe.value = rememberMe
            
            if (rememberMe && _user.value != null) {
                // Simpan ke SQLite
                withContext(Dispatchers.IO) {
                    val userData = _user.value!!
                    dbHelper.saveUser(
                        userId = userData.id,
                        nama = userData.nama,
                        email = userData.email,
                        phone = userData.phone,
                        address = userData.address,
                        token = token,
                        rememberMe = true
                    )
                }
            } else if (!rememberMe) {
                // Jika Remember Me tidak aktif, hapus dari database
                withContext(Dispatchers.IO) {
                    dbHelper.clearUser()
                }
            }
            
            if (_user.value != null) {
                _isLoading.value = false
            }
        }
    }

    /**
     * Simpan remember me preference
     */
    fun saveRememberMe(remember: Boolean) {
        viewModelScope.launch {
            _rememberMe.value = remember
            
            if (remember && _user.value != null && _token.value != null) {
                // Simpan ke SQLite
                withContext(Dispatchers.IO) {
                    val userData = _user.value!!
                    dbHelper.saveUser(
                        userId = userData.id,
                        nama = userData.nama,
                        email = userData.email,
                        phone = userData.phone,
                        address = userData.address,
                        token = _token.value,
                        rememberMe = true
                    )
                }
            } else if (!remember) {
                // Hapus dari database
                withContext(Dispatchers.IO) {
                    dbHelper.clearUser()
                }
            }
        }
    }

    /**
     * Set user data langsung (dari LoginResponse)
     */
    fun setUserData(userData: UserData?) {
        _user.value = userData
        _isLoading.value = false
        
        // Jika Remember Me aktif, simpan user data ke SQLite
        if (_rememberMe.value && userData != null && _token.value != null) {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    dbHelper.saveUser(
                        userId = userData.id,
                        nama = userData.nama,
                        email = userData.email,
                        phone = userData.phone,
                        address = userData.address,
                        token = _token.value,
                        rememberMe = true
                    )
                }
            }
        }
    }

    /**
     * Fetch user data setelah login berhasil
     */
    fun fetchUserDataAfterLogin(token: String) {
        _isLoading.value = true
        ApiClient.instance.getSession(token).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful && response.body()?.status == "success") {
                    val userData = response.body()?.user
                    _user.value = userData
                    
                    // Simpan ke SQLite jika Remember Me aktif
                    if (_rememberMe.value && userData != null) {
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.saveUser(
                                    userId = userData.id,
                                    nama = userData.nama,
                                    email = userData.email,
                                    phone = userData.phone,
                                    address = userData.address,
                                    token = token,
                                    rememberMe = true
                                )
                            }
                        }
                    }
                } else {
                    _user.value = null
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                _isLoading.value = false
                _user.value = null
            }
        })
    }

    /**
     * Ambil user dari session token (saat startup) - background refresh
     */
    private fun fetchUserData(token: String) {
        ApiClient.instance.getSession(token).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(
                call: Call<SessionResponse>,
                response: Response<SessionResponse>
            ) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val userData = response.body()?.user
                    _user.value = userData
                    
                    // Update data di SQLite
                    if (_rememberMe.value && userData != null) {
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.saveUser(
                                    userId = userData.id,
                                    nama = userData.nama,
                                    email = userData.email,
                                    phone = userData.phone,
                                    address = userData.address,
                                    token = token,
                                    rememberMe = true
                                )
                            }
                        }
                    }
                } else {
                    val errorCode = response.code()
                    if (errorCode == 401 || errorCode == 403) {
                        // Token invalid/expired - clear session
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.clearUser()
                            }
                            _token.value = null
                            _user.value = null
                        }
                    }
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                // Network error - keep existing user data from SQLite
            }
        })
    }

    /**
     * Logout - hapus semua data user dari SQLite
     */
    fun logout() {
        viewModelScope.launch {
            // Hapus data dari SQLite
            withContext(Dispatchers.IO) {
                dbHelper.clearUser()
            }
            
            // Reset state
            _token.value = null
            _user.value = null
            _rememberMe.value = false
        }
    }

    // --- State untuk Update Personal Information ---
    private val _updateStatus = MutableStateFlow<String?>(null)
    val updateStatus: StateFlow<String?> get() = _updateStatus

    private val _updateMessage = MutableStateFlow<String?>(null)
    val updateMessage: StateFlow<String?> get() = _updateMessage

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating: StateFlow<Boolean> get() = _isUpdating

    /**
     * Update personal information ke server
     */
    fun updatePersonalInfo(
        nama: String,
        email: String,
        phone: String,
        address: String?,
        currentPassword: String? = null,  // Keep for compatibility but ignored
        newPassword: String? = null,      // This becomes the password
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val currentToken = _token.value
        if (currentToken.isNullOrEmpty()) {
            _updateStatus.value = "error"
            _updateMessage.value = "Token tidak ditemukan. Silakan login ulang."
            onError("Token tidak ditemukan")
            return
        }

        _isUpdating.value = true
        _updateStatus.value = null
        _updateMessage.value = null

        val request = UpdatePersonalInfoRequest(
            token = currentToken,
            nama = nama,
            email = email,
            phone = phone,
            address = address,
            password = newPassword  // Use newPassword as password
        )

        ApiClient.instance.updatePersonalInfo(currentToken, request).enqueue(object : Callback<UpdatePersonalInfoResponse> {
            override fun onResponse(
                call: Call<UpdatePersonalInfoResponse>,
                response: Response<UpdatePersonalInfoResponse>
            ) {
                _isUpdating.value = false
                if (response.isSuccessful && response.body()?.status == "success") {
                    _updateStatus.value = "success"
                    _updateMessage.value = response.body()?.message ?: "Data berhasil diperbarui"
                    
                    // Update user data di state
                    val updatedUser = response.body()?.user ?: UserData(
                        id = _user.value?.id,
                        nama = nama,
                        email = email,
                        phone = phone,
                        address = address
                    )
                    _user.value = updatedUser
                    
                    // Simpan ke SQLite jika Remember Me aktif
                    if (_rememberMe.value) {
                        viewModelScope.launch {
                            withContext(Dispatchers.IO) {
                                dbHelper.saveUser(
                                    userId = updatedUser.id,
                                    nama = updatedUser.nama,
                                    email = updatedUser.email,
                                    phone = updatedUser.phone,
                                    address = updatedUser.address,
                                    token = currentToken,
                                    rememberMe = true
                                )
                            }
                        }
                    }
                    
                    onSuccess()
                } else {
                    _updateStatus.value = "error"
                    _updateMessage.value = response.body()?.message ?: "Gagal memperbarui data"
                    onError(_updateMessage.value ?: "Unknown error")
                }
            }

            override fun onFailure(call: Call<UpdatePersonalInfoResponse>, t: Throwable) {
                _isUpdating.value = false
                _updateStatus.value = "error"
                _updateMessage.value = "Koneksi gagal: ${t.message}"
                onError(_updateMessage.value ?: "Network error")
            }
        })
    }

    /**
     * Reset update status
     */
    fun resetUpdateStatus() {
        _updateStatus.value = null
        _updateMessage.value = null
    }
}


