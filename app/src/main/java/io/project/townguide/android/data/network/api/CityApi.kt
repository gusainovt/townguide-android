package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.CityCreateRequest
import io.project.townguide.android.data.network.dto.CityResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CityApi {

    @GET("api/v1/city")
    suspend fun getCities(): List<CityResponse>

    @POST("api/v1/city")
    suspend fun createCity(
        @Body request: CityCreateRequest
    ): CityResponse
}
