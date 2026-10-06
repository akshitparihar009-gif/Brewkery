package com.akshit.brewkery.data.repository

import com.akshit.brewkery.data.model.CartItem
import com.akshit.brewkery.data.model.MilkOption
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.data.model.OrderStatus
import com.akshit.brewkery.data.model.SizeOption
import com.akshit.brewkery.data.model.StoreMeta
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

object CartRepository {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    private val _storeMeta = MutableStateFlow(StoreMeta())
    val storeMeta: StateFlow<StoreMeta> = _storeMeta.asStateFlow()

    fun updateMeta(meta: StoreMeta) {
        _storeMeta.value = meta
    }

    fun addToCart(
        menuItem: MenuItem,
        selectedSize: SizeOption?,
        selectedMilk: MilkOption?,
        selectedSugar: String?,
        quantity: Int
    ) {
        val extraSizePrice = selectedSize?.extraPrice ?: 0.0
        val extraMilkPrice = selectedMilk?.extraPrice ?: 0.0
        val unitPrice = menuItem.basePrice + extraSizePrice + extraMilkPrice

        val cartItemId = "${menuItem.id}-${selectedSize?.id ?: "def"}-${selectedMilk?.id ?: "def"}-${selectedSugar ?: "def"}"

        _cartItems.update { currentItems ->
            val existingIndex = currentItems.indexOfFirst { it.cartItemId == cartItemId }
            if (existingIndex >= 0) {
                val existing = currentItems[existingIndex]
                val newQty = existing.quantity + quantity
                val updated = existing.copy(
                    quantity = newQty,
                    totalPrice = unitPrice * newQty
                )
                currentItems.toMutableList().apply { set(existingIndex, updated) }
            } else {
                val newItem = CartItem(
                    cartItemId = cartItemId,
                    menuItem = menuItem,
                    selectedSize = selectedSize,
                    selectedMilk = selectedMilk,
                    selectedSugar = selectedSugar,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalPrice = unitPrice * quantity
                )
                currentItems + newItem
            }
        }
    }

    fun updateQuantity(cartItemId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(cartItemId)
            return
        }
        _cartItems.update { currentItems ->
            currentItems.map { item ->
                if (item.cartItemId == cartItemId) {
                    item.copy(
                        quantity = newQuantity,
                        totalPrice = item.unitPrice * newQuantity
                    )
                } else {
                    item
                }
            }
        }
    }

    fun incrementQuantity(cartItemId: String) {
        val item = _cartItems.value.find {
            it.cartItemId == cartItemId
        } ?: return

        // Maximum quantity allowed is 30
        if (item.quantity < 30) {
            updateQuantity(
                cartItemId,
                item.quantity + 1
            )
        }
    }
    fun decrementQuantity(cartItemId: String) {
        val item = _cartItems.value.find { it.cartItemId == cartItemId } ?: return
        updateQuantity(cartItemId, item.quantity - 1)
    }

    fun removeFromCart(cartItemId: String) {
        _cartItems.update { currentItems ->
            currentItems.filterNot { it.cartItemId == cartItemId }
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun placeOrder(): Order {
        val items = _cartItems.value
        val meta = _storeMeta.value

        val subtotal = items.sumOf { it.totalPrice }
        val deliveryFee = meta.deliveryFee
        val tax = subtotal * (meta.taxRatePercent / 100.0)
        val total = subtotal + deliveryFee + tax

        val randomId = Random.nextInt(10000, 99999)
        val ticketId = "#BK-$randomId"

        val order = Order(
            ticketId = ticketId,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            tax = tax,
            total = total,
            status = OrderStatus.PREPARING,
            estimatedWaitTime = meta.estimatedDeliveryTime
        )

        _activeOrder.value = order
        _cartItems.value = emptyList()
        return order
    }

    fun clearActiveOrder() {
        _activeOrder.value = null
    }
}
