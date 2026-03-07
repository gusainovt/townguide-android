package io.project.townguide.android.data.network.dto

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName(value = "token", alternate = ["accessToken"])
    val token: String,
    @SerializedName(value = "refreshToken")
    val refreshToken: String? = null
)
