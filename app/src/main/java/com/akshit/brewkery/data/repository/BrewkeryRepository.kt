package com.akshit.brewkery.data.repository

import com.akshit.brewkery.data.api.BrewkeryApiService
import com.akshit.brewkery.data.api.RetrofitClient
import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.MenuResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrewkeryRepository(
    private val apiService: BrewkeryApiService = RetrofitClient.apiService
) {

    suspend fun getMenu(): Result<MenuResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMenu()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch menu: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemDetail(id: Int): Result<MenuItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getItemDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch item details: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
