package io.project.townguide.android.data.network.api

import io.project.townguide.android.data.network.dto.StoryCreateRequest
import io.project.townguide.android.data.network.dto.StoryDto
import retrofit2.http.Body
import retrofit2.http.POST

interface StoriesApi {

    @POST("api/v1/stories")
    suspend fun createStory(
        @Body request: StoryCreateRequest
    ): StoryDto
}
