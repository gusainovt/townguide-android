package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.PlaceCreateResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface PlaceApi {

    @Multipart
    @POST("api/v1/places")
    suspend fun createPlace(
        @Part("data") data: RequestBody,
        @Part file: MultipartBody.Part
    ): PlaceCreateResponse
}
