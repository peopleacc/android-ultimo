package com.ultimo.vehicleapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.PemesananInsertRepository
import com.ultimo.vehicleapp.Controller.PemesananRepository
import com.ultimo.vehicleapp.Controller.ProductRepository
import com.ultimo.vehicleapp.model.PemesananInsert
import com.ultimo.vehicleapp.model.ProductLayanan
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

    private fun loadPemesanan() {
        viewModelScope.launch {
            _pemesanan.value = PemesananRepository.getAllPemesanan()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertOrder(
        userId: Int,
        selectedDesign: Int,
        selectedMaterial: Int,
        totalPrice: Int
    ): Boolean {
        val currentDate = java.time.LocalDate.now().toString()

        val data = PemesananInsert(
            user_id = userId,
            product_id = selectedDesign,
            bahan_id = selectedMaterial,
            total_estimasi_harga = totalPrice,
            tanggal_pesan = currentDate
        )

        return PemesananInsertRepository.insertPemesanan(data)
    }
}

