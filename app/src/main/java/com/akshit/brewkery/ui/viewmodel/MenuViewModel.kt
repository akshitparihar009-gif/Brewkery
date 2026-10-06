package com.akshit.brewkery.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.akshit.brewkery.data.model.Category
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.Order
import com.akshit.brewkery.data.model.StoreMeta
import com.akshit.brewkery.data.repository.BrewkeryRepository
import com.akshit.brewkery.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface MenuUiState {
    data object Loading : MenuUiState
    data class Success(
        val meta: StoreMeta,
        val categories: List<Category>,
        val allItems: List<MenuItem>,
        val filteredItems: List<MenuItem>,
        val selectedCategoryId: String,
        val searchQuery: String
    ) : MenuUiState
    data class Error(val message: String) : MenuUiState
}

class MenuViewModel(
    private val repository: BrewkeryRepository = BrewkeryRepository(),
    private val cartRepository: CartRepository = CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    val cartItems = cartRepository.cartItems
    val activeOrder: StateFlow<Order?> = cartRepository.activeOrder

    val cartSubtotal: StateFlow<Double> = combine(cartItems) { items ->
        items[0].sumOf { it.totalPrice }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartItemCount: StateFlow<Int> = combine(cartItems) { items ->
        items[0].sumOf { it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            _uiState.value = MenuUiState.Loading
            val result = repository.getMenu()
            result.onSuccess { response ->
                cartRepository.updateMeta(response.meta)

                val allCategory = Category(
                    id = "ALL",
                    name = "All Items",
                    icon = "☕",
                    itemCount = response.items.size
                )
                val categoryList = listOf(allCategory) + response.categories

                _uiState.value = MenuUiState.Success(
                    meta = response.meta,
                    categories = categoryList,
                    allItems = response.items,
                    filteredItems = response.items,
                    selectedCategoryId = "ALL",
                    searchQuery = ""
                )
            }.onFailure { error ->
                _uiState.value = MenuUiState.Error(
                    error.localizedMessage ?: "Failed to load menu. Please check your network connection."
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        val currentState = _uiState.value
        if (currentState is MenuUiState.Success) {
            val updatedFiltered = filterItems(currentState.allItems, categoryId, currentState.searchQuery)
            _uiState.value = currentState.copy(
                selectedCategoryId = categoryId,
                filteredItems = updatedFiltered
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        if (currentState is MenuUiState.Success) {
            val updatedFiltered = filterItems(currentState.allItems, currentState.selectedCategoryId, query)
            _uiState.value = currentState.copy(
                searchQuery = query,
                filteredItems = updatedFiltered
            )
        }
    }

    private fun filterItems(
        items: List<MenuItem>,
        categoryId: String,
        query: String
    ): List<MenuItem> {
        return items.filter { item ->
            val matchesCategory = if (categoryId == "ALL") true else item.categoryId == categoryId
            val matchesQuery = if (query.isBlank()) true else {
                item.name.contains(query, ignoreCase = true) ||
                        item.tagline.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }
}
