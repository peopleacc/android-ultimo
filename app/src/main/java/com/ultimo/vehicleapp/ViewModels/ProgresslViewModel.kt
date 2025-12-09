package com.ultimo.vehicleapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.AllProgressRepository
import com.ultimo.vehicleapp.Controller.ProgressRepository
import com.ultimo.vehicleapp.Controller.TotalProsesProgress
import com.ultimo.vehicleapp.Controller.getTotalProsesFromProgress
import com.ultimo.vehicleapp.model.Progress
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProgressViewModel : ViewModel() {

    private val _progress = MutableStateFlow<List<Progress>>(emptyList())
    val progress: StateFlow<List<Progress>> = _progress   // FIXED

    private val _activeProgress = MutableStateFlow<List<Progress>>(emptyList())
    val activeProgress: StateFlow<List<Progress>> = _activeProgress

    private val _completedProgress = MutableStateFlow<List<Progress>>(emptyList())
    val completedProgress: StateFlow<List<Progress>> = _completedProgress

    // StateFlow untuk total proses dari progress
    private val _totalProsesProgress = MutableStateFlow(0)
    val totalProsesProgress: StateFlow<Int> = _totalProsesProgress

    /**
     * Load progress pemesanan user
     */
    fun loadProgressForUser(userId: Int, limit: Int = 10) {
        viewModelScope.launch {
            val data = ProgressRepository
                .getAllProgressPemesananUser(userId.toString())
                .reversed()
                .take(limit)

            _progress.value = data
        }
    }

    fun loadAllProgressForUser(userId: Int, limit: Int = 10) {
        viewModelScope.launch {
            val dataProgress = AllProgressRepository
                .getAllProgressUser(userId.toString())
                .reversed()
                .take(limit)

            _progress.value = dataProgress
        }
    }

    /**
     * Load active progress (pending, proses, waiting for payment)
     */
    fun loadActiveProgressForUser(userId: Int, limit: Int = 10) {
        viewModelScope.launch {
            val data = ProgressRepository
                .getAllProgressPemesananUser(userId.toString())
                .reversed()
                .take(limit)
                .filter { progress ->
                    val status = progress.t_pemesanan?.status_pengerjaan?.lowercase()
                    status == "pending" || status == "proses" || 
                    status == "waiting for payment" || status == "waiting to payment"
                }

            _activeProgress.value = data
        }
    }

    /**
     * Load completed progress (selesai)
     */
    fun loadCompletedProgressForUser(userId: Int, limit: Int = 50) {
        viewModelScope.launch {
            val dataProgress = AllProgressRepository
                .getAllProgressUser(userId.toString())
                .reversed()
                .filter { progress ->
                    val status = progress.t_pemesanan?.status_pengerjaan?.lowercase()
                    status == "selesai" || status == "completed"
                }
                .take(limit)

            _completedProgress.value = dataProgress
        }
    }

    /**
     * Load total proses dari ProgressController (status_pengerjaan = "proses")
     */
    fun loadTotalProsesFromProgress(userId: Int) {
        viewModelScope.launch {
            val result = getTotalProsesFromProgress(userId.toString())
            _totalProsesProgress.value = result.firstOrNull()?.total_proses_progress ?: 0
        }
    }
}

