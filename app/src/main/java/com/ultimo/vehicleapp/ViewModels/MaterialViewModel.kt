package com.ultimo.vehicleapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.MaterialRepository
import com.ultimo.vehicleapp.Controller.ProductRepository
import com.ultimo.vehicleapp.model.Material_List
import com.ultimo.vehicleapp.model.ProductLayanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaterialRepository : ViewModel() {

    private val _material = MutableStateFlow<List<Material_List>>(emptyList())
    val Material_List: StateFlow<List<Material_List>> = _material

    init {
        loadMaterial()
    }

    private fun loadMaterial() {
        viewModelScope.launch {
            _material.value = MaterialRepository.getAllMaterial()
        }
    }
}