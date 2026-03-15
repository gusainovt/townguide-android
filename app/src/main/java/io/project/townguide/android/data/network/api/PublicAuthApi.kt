package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.LoginRequest
import io.project.townguide.android.data.network.dto.LoginResponse
import io.project.townguide.android.data.network.dto.RefreshRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface PublicAuthApi {

    @POST("/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("/auth/refresh")
    suspend fun refresh(
        @Body request: RefreshRequest
    ): LoginResponse
}
