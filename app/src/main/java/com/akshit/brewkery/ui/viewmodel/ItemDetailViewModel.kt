package com.akshit.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.MilkOption
import com.akshit.brewkery.data.model.SizeOption
import com.akshit.brewkery.data.repository.BrewkeryRepository
import com.akshit.brewkery.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ItemDetailUiState {
    data object Loading : ItemDetailUiState
    data class Success(
        val menuItem: MenuItem,
        val selectedSize: SizeOption?,
        val selectedMilk: MilkOption?,
        val selectedSugar: String?,
        val quantity: Int,
        val unitPrice: Double,
        val totalPrice: Double
    ) : ItemDetailUiState
    data class Error(val message: String) : ItemDetailUiState
}

class ItemDetailViewModel(
    private val repository: BrewkeryRepository = BrewkeryRepository(),
    private val cartRepository: CartRepository = CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ItemDetailUiState>(ItemDetailUiState.Loading)
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItemDetail(itemId: Int) {
        viewModelScope.launch {
            _uiState.value = ItemDetailUiState.Loading
            val result = repository.getItemDetail(itemId)
            result.onSuccess { item ->
                val defaultSize = item.customizations?.sizes?.firstOrNull()
                val defaultMilk = item.customizations?.milkOptions?.firstOrNull()
                val defaultSugar = item.customizations?.sugarLevels?.firstOrNull()
                val qty = 1

                val unitPrice = calculateUnitPrice(item.basePrice, defaultSize, defaultMilk)
                val totalPrice = unitPrice * qty

                _uiState.value = ItemDetailUiState.Success(
                    menuItem = item,
                    selectedSize = defaultSize,
                    selectedMilk = defaultMilk,
                    selectedSugar = defaultSugar,
                    quantity = qty,
                    unitPrice = unitPrice,
                    totalPrice = totalPrice
                )
            }.onFailure { error ->
                _uiState.value = ItemDetailUiState.Error(
                    error.localizedMessage ?: "Failed to load item details."
                )
            }
        }
    }

    fun selectSize(sizeOption: SizeOption) {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success) {
            val unitPrice = calculateUnitPrice(currentState.menuItem.basePrice, sizeOption, currentState.selectedMilk)
            val totalPrice = unitPrice * currentState.quantity
            _uiState.value = currentState.copy(
                selectedSize = sizeOption,
                unitPrice = unitPrice,
                totalPrice = totalPrice
            )
        }
    }

    fun selectMilk(milkOption: MilkOption) {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success) {
            val unitPrice = calculateUnitPrice(currentState.menuItem.basePrice, currentState.selectedSize, milkOption)
            val totalPrice = unitPrice * currentState.quantity
            _uiState.value = currentState.copy(
                selectedMilk = milkOption,
                unitPrice = unitPrice,
                totalPrice = totalPrice
            )
        }
    }

    fun selectSugar(sugar: String) {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success) {
            _uiState.value = currentState.copy(selectedSugar = sugar)
        }
    }

    fun incrementQuantity() {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success) {
            val newQty = currentState.quantity + 1
            _uiState.value = currentState.copy(
                quantity = newQty,
                totalPrice = currentState.unitPrice * newQty
            )
        }
    }

    fun decrementQuantity() {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success && currentState.quantity > 1) {
            val newQty = currentState.quantity - 1
            _uiState.value = currentState.copy(
                quantity = newQty,
                totalPrice = currentState.unitPrice * newQty
            )
        }
    }

    fun addToCart(): Boolean {
        val currentState = _uiState.value
        if (currentState is ItemDetailUiState.Success) {
            cartRepository.addToCart(
                menuItem = currentState.menuItem,
                selectedSize = currentState.selectedSize,
                selectedMilk = currentState.selectedMilk,
                selectedSugar = currentState.selectedSugar,
                quantity = currentState.quantity
            )
            return true
        }
        return false
    }

    private fun calculateUnitPrice(
        basePrice: Double,
        sizeOption: SizeOption?,
        milkOption: MilkOption?
    ): Double {
        val extraSize = sizeOption?.extraPrice ?: 0.0
        val extraMilk = milkOption?.extraPrice ?: 0.0
        return basePrice + extraSize + extraMilk
    }
}
