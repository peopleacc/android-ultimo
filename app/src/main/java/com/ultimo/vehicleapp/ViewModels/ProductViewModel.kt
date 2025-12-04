package com.ultimo.vehicleapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ultimo.vehicleapp.Controller.ProductRepository
import com.ultimo.vehicleapp.model.ProductLayanan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val _products = MutableStateFlow<List<ProductLayanan>>(emptyList())
    val products: StateFlow<List<ProductLayanan>> = _products

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _products.value = ProductRepository.getAllProducts()
        }
    }
}
