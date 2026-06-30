package com.learnit.app.repository

import com.learnit.app.model.ProductDetailsDto
import com.learnit.app.util.ApiState

interface Repository {
    suspend fun getProductDetails(): ApiState<ProductDetailsDto>
}