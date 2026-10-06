package com.akshit.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshit.brewkery.data.model.CartItem
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.data.model.StoreMeta
import com.akshit.brewkery.data.repository.CartRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CartViewModel(
    private val cartRepository: CartRepository = CartRepository
) : ViewModel() {

    val cartItems: StateFlow<List<CartItem>> = cartRepository.cartItems
    val storeMeta: StateFlow<StoreMeta> = cartRepository.storeMeta

    val subtotal: StateFlow<Double> = combine(cartItems) { items ->
        items[0].sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val deliveryFee: StateFlow<Double> = combine(storeMeta) { meta ->
        meta[0].deliveryFee
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2.50)

    val tax: StateFlow<Double> = combine(subtotal, storeMeta) { sub, meta ->
        sub * (meta.taxRatePercent / 100.0)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val total: StateFlow<Double> = combine(subtotal, deliveryFee, tax) { sub, del, t ->
        if (sub > 0) sub + del + t else 0.0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun incrementQuantity(cartItemId: String) {
        cartRepository.incrementQuantity(cartItemId)
    }

    fun decrementQuantity(cartItemId: String) {
        cartRepository.decrementQuantity(cartItemId)
    }

    fun removeFromCart(cartItemId: String) {
        cartRepository.removeFromCart(cartItemId)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }

    fun placeOrder(): Order {
        return cartRepository.placeOrder()
    }
}
