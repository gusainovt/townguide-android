package io.project.townguide.android.data.network.api

import io.project.townguide.android.BuildConfig
import io.project.townguide.android.TownguideApp.Companion.appContext
import io.project.townguide.android.data.network.AuthInterceptor
import io.project.townguide.android.data.storage.TokenStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val DEFAULT_BASE_URL = "http://10.0.2.2:8080/"
    private val baseUrl = BuildConfig.API_BASE_URL.ifBlank { DEFAULT_BASE_URL }

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(TokenStorage(appContext)))
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val cityApi: CityApi by lazy {
        retrofit.create(CityApi::class.java)
    }

    val storiesApi: StoriesApi by lazy {
        retrofit.create(StoriesApi::class.java)
    }
}
