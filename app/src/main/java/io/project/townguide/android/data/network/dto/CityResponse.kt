package io.project.townguide.android.data.network.dto

data class CityResponse(
    val id: Long,
    val name: String,
    val nameEng: String,
    val description: String,
    val callback: String,
    val photo: String?,
    val places: List<PlaceDto>,
    val stories: List<StoryDto>
)
