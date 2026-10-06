package com.akshit.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.data.repository.CartRepository
import kotlinx.coroutines.flow.StateFlow

class OrderViewModel(
    private val cartRepository: CartRepository = CartRepository
) : ViewModel() {

    val activeOrder: StateFlow<Order?> = cartRepository.activeOrder

    fun clearOrder() {
        cartRepository.clearActiveOrder()
    }
}
