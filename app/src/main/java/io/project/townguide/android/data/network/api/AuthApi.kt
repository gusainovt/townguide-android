package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.AdminProfileResponse
import io.project.townguide.android.data.network.dto.ChangePasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @GET("/auth/me")
    suspend fun me(): Response<AdminProfileResponse>

    @POST("/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<Unit>
}
