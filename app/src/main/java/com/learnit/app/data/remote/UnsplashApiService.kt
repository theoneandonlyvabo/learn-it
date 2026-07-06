package com.learnit.app.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface UnsplashApiService {
    // Authorization header is "Client-ID <accessKey>".
    @GET("search/photos")
    suspend fun search(
        @Query("query") query: String,
        @Header("Authorization") authorization: String,
        @Query("per_page") perPage: Int = 1,
        @Query("orientation") orientation: String = "squarish"
    ): UnsplashSearchResponse
}

@Serializable
data class UnsplashSearchResponse(
    val results: List<UnsplashPhoto> = emptyList()
)

@Serializable
data class UnsplashPhoto(
    val urls: UnsplashUrls
)

@Serializable
data class UnsplashUrls(
    @SerialName("small") val small: String
)
