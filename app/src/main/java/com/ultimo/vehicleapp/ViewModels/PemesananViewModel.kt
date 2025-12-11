package com.ultimo.vehicleapp.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.PemesananDeleteRepository
import com.ultimo.vehicleapp.Controller.PemesananInsertRepository
import com.ultimo.vehicleapp.Controller.PemesananRepository
import com.ultimo.vehicleapp.Controller.PemesananUserRepository
import com.ultimo.vehicleapp.Controller.TotalSelesai
import com.ultimo.vehicleapp.Controller.TotalProses
import com.ultimo.vehicleapp.Controller.TotalPending
import com.ultimo.vehicleapp.Controller.getTotalSelesai
import com.ultimo.vehicleapp.Controller.getTotalProses
import com.ultimo.vehicleapp.Controller.getTotalPending
import com.ultimo.vehicleapp.Controller.UploadGambarRepository
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.pemesanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PemesananViewModel : ViewModel() {

    private val _pemesanan = MutableStateFlow<List<pemesanan>>(emptyList())
    val pemesanan: StateFlow<List<pemesanan>> = _pemesanan

    // StateFlow untuk total selesai
    private val _totalSelesai = MutableStateFlow(0)
    val totalSelesai: StateFlow<Int> = _totalSelesai

    // StateFlow untuk total proses
    private val _totalProses = MutableStateFlow(0)
    val totalProses: StateFlow<Int> = _totalProses

    // StateFlow untuk total pending (pending + waiting for order)
    private val _totalPending = MutableStateFlow(0)
    val totalPending: StateFlow<Int> = _totalPending


    // Upload State untuk tracking proses upload
    sealed class UploadState {
        object Idle : UploadState()
        object Loading : UploadState()
        data class Success(val imageUrl: String) : UploadState()
        data class Error(val message: String) : UploadState()
    }

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState: StateFlow<UploadState> = _uploadState

    init {
        loadPemesanan()
    }

    /**
     * Load semua pemesanan (biasanya untuk admin)
     */
    private fun loadPemesanan() {
        viewModelScope.launch {
            _pemesanan.value = PemesananRepository.getAllPemesanan()
        }
    }

    /**
     * Load pemesanan milik user tertentu (newest first)
     */
    fun loadPemesananForUser(userId: Int, limit: Int = 1) {
        viewModelScope.launch {
            _pemesanan.value =
                PemesananUserRepository.getAllPemesananUser(userId.toString()).reversed().take(limit)
        }
    }

    /**
     * Load total pemesanan selesai untuk user tertentu
     */
    fun loadTotalSelesai(userId: Int) {
        viewModelScope.launch {
            val result = getTotalSelesai(userId.toString())
            _totalSelesai.value = result.firstOrNull()?.total_selesai ?: 0
        }
    }

    /**
     * Load total pemesanan proses untuk user tertentu
     */
    fun loadTotalProses(userId: Int) {
        viewModelScope.launch {
            val result = getTotalProses(userId.toString())
            _totalProses.value = result.firstOrNull()?.total_proses ?: 0
        }
    }

    /**
     * Load total pemesanan pending (pending + waiting for order) untuk user tertentu
     * Data berasal dari PemesananController
     */
    fun loadTotalPending(userId: Int) {
        viewModelScope.launch {
            val result = getTotalPending(userId.toString())
            _totalPending.value = result.firstOrNull()?.total_pending ?: 0
        }
    }


    /**
     * Insert pemesanan baru
     * Return: pemesanan terbaru user (atau null jika gagal)
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertOrder(
        userId: Int,
        selectedDesign: Int,
        statuspengerjaan: String,
        selectedMaterial: Int,
        totalPrice: Int
    ): pemesanan? {

        val currentDate = java.time.LocalDate.now().toString()

        val data = PemesananInsert(
            user_id = userId,
            product_id = selectedDesign,
            status_pengerjaan = statuspengerjaan,
            bahan_id = selectedMaterial,
            total_estimasi_harga = totalPrice,
            tanggal_pesan = currentDate
        )

        // Insert
        val success = PemesananInsertRepository.insertPemesanan(data)

        if (!success) return null

        // Reload daftar pemesanan user
        loadPemesananForUser(userId)

        // Ambil pemesanan terbaru user (1 data saja)
        val latestList = PemesananUserRepository.getAllPemesananUser(userId.toString())

        return latestList.firstOrNull()
    }

    /**
     * Delete pemesanan by pesanan_id
     * Return: true if successful, false otherwise
     */
    fun deletePemesanan(pesananId: Int, userId: Int) {
        viewModelScope.launch {
            val success = PemesananDeleteRepository.deletePemesananById(pesananId)
            if (success) {
                // Reload pemesanan after deletion
                loadPemesananForUser(userId, limit = 20)
            }
        }
    }

    /**
     * Upload bukti pembayaran ke Supabase Storage dan update upload_gambar di database
     * Status pembayaran TIDAK diubah (tetap menunggu pembayaran)
     * @param pesananId ID pesanan
     * @param imageBytes Data gambar dalam ByteArray
     * @param fileExtension Ekstensi file (default: jpg)
     */
    fun uploadBuktiPembayaran(
        pesananId: Int,
        imageBytes: ByteArray,
        fileExtension: String = "jpg"
    ) {
        viewModelScope.launch {
            _uploadState.value = UploadState.Loading
            
            try {
                // Upload gambar ke Supabase Storage
                val imageUrl = UploadGambarRepository.uploadGambar(
                    pesananId = pesananId,
                    imageBytes = imageBytes,
                    fileExtension = fileExtension
                )
                
                if (imageUrl != null) {
                    // Update kolom upload_gambar di database
                    val updateSuccess = UploadGambarRepository.updateUploadGambar(
                        pesananId = pesananId,
                        imageUrl = imageUrl
                    )
                    
                    if (updateSuccess) {
                        _uploadState.value = UploadState.Success(imageUrl)
                    } else {
                        _uploadState.value = UploadState.Error("Gagal menyimpan URL gambar ke database")
                    }
                } else {
                    _uploadState.value = UploadState.Error("Gagal mengupload gambar ke storage")
                }
            } catch (e: Exception) {
                _uploadState.value = UploadState.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Reset upload state ke Idle
     */
    fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }
}

