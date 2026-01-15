package io.project.townguide.android.data.network

import io.project.townguide.android.data.network.dto.LoginRequest
import io.project.townguide.android.data.network.dto.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}