package com.learnit.app.repository

import com.learnit.app.model.ProductDetailsDto
import com.learnit.app.network.ApiService
import com.learnit.app.util.ApiState
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {

    override suspend fun getProductDetails(): ApiState<ProductDetailsDto> = try {
        ApiState.Success(apiService.getProductDetails())
    } catch (e: Exception) {
        ApiState.Error(errorMsg = e.message.toString())
    }
}