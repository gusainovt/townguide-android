package io.project.townguide.android.data.network.dto

data class PlaceCreateRequest(
    val cityId: Long,
    val name: String,
    val description: String
)
