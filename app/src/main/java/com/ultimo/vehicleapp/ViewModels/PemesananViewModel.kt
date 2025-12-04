package com.ultimo.vehicleapp.ViewModels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.PemesananInsertRepository
import com.ultimo.vehicleapp.Controller.PemesananRepository
import com.ultimo.vehicleapp.Controller.PemesananUserRepository
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.pemesanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PemesananViewModel : ViewModel() {

    private val _pemesanan = MutableStateFlow<List<pemesanan>>(emptyList())
    val pemesanan: StateFlow<List<pemesanan>> = _pemesanan

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
    fun loadPemesananForUser(userId: Int, limit: Int = 20) {
        viewModelScope.launch {
            _pemesanan.value =
                PemesananUserRepository.getAllPemesananUser(userId.toString()).reversed().take(limit)
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
}
