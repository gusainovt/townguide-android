package io.project.townguide.android.data.network

import io.project.townguide.android.data.network.api.PublicAuthApi
import io.project.townguide.android.data.network.dto.RefreshRequest
import io.project.townguide.android.data.session.SessionEvents
import io.project.townguide.android.data.storage.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenStorage: TokenStorage,
    private val publicAuthApi: PublicAuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val path = response.request.url.encodedPath
        if (path == "/auth/login" || path == "/auth/refresh") return null

        synchronized(this) {
            val latestToken = runBlocking { tokenStorage.token.first() }
            val requestToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")

            if (!latestToken.isNullOrBlank() && latestToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $latestToken")
                    .build()
            }

            val refreshToken = runBlocking { tokenStorage.refreshToken.first() }
            if (refreshToken.isNullOrBlank()) {
                invalidateSession()
                return null
            }

            return try {
                val tokenResponse = runBlocking {
                    publicAuthApi.refresh(RefreshRequest(refreshToken))
                }
                runBlocking {
                    tokenStorage.saveTokens(tokenResponse.token, tokenResponse.refreshToken)
                }

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.token}")
                    .build()
            } catch (_: Exception) {
                invalidateSession()
                null
            }
        }
    }

    private fun invalidateSession() {
        runBlocking { tokenStorage.clear() }
        SessionEvents.notifySessionExpired()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            result++
            priorResponse = priorResponse.priorResponse
        }
        return result
    }
}
