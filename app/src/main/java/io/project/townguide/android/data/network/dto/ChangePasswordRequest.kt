package io.project.townguide.android.data.network.dto

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
