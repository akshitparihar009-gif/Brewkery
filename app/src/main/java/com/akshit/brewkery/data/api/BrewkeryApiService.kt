package com.akshit.brewkery.data.api

import com.akshit.brewkery.data.model.MenuItem
import com.akshit.brewkery.data.model.MenuResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface BrewkeryApiService {

    @GET("main/data.json")
    suspend fun getMenu(): Response<MenuResponse>

    @GET("main/api/items/{id}.json")
    suspend fun getItemDetail(@Path("id") id: Int): Response<MenuItem>
}
