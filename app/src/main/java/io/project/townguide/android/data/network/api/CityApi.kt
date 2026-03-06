package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.CityResponse
import retrofit2.http.GET

interface CityApi {

    @GET("/api/v1/cities")
    suspend fun getCities(): List<CityResponse>
}
