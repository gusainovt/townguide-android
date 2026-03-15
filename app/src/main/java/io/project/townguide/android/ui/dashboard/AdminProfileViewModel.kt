package io.project.townguide.android.ui.dashboard

import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import io.project.townguide.android.data.network.ApiErrorMessageExtractor
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.AdminProfileResponse
import io.project.townguide.android.data.network.dto.ChangePasswordRequest
import io.project.townguide.android.data.storage.TokenStorage
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class AdminProfileUiState(
    val name: String = "Администратор",
    val login: String = "Неизвестно",
    val role: String = "Неизвестно",
    val loading: Boolean = false,
    val error: String? = null,
    val currentPassword: String = "",
    val newPassword: String = "",
    val passwordChangeLoading: Boolean = false,
    val passwordChangeMessage: String? = null
)

class AdminProfileViewModel(
    private val tokenStorage: TokenStorage
) : ViewModel() {

    private val _state = MutableStateFlow(AdminProfileUiState())
    val state = _state.asStateFlow()

    init {
        loadProfile()
    }

    fun onCurrentPasswordChanged(value: String) {
        _state.value = _state.value.copy(
            currentPassword = value,
            passwordChangeMessage = null
        )
    }

    fun onNewPasswordChanged(value: String) {
        _state.value = _state.value.copy(
            newPassword = value,
            passwordChangeMessage = null
        )
    }

    fun loadProfile() {
        viewModelScope.launch {
            val fallback = decodeProfileFromToken()
            _state.value = _state.value.copy(
                loading = true,
                error = null,
                name = fallback.name,
                login = fallback.login,
                role = fallback.role
            )

            try {
                val response = ApiClient.authApi.me()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        applyProfile(body)
                    } else {
                        _state.value = _state.value.copy(
                            loading = false,
                            error = "Backend вернул пустой профиль."
                        )
                    }
                } else {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = ApiErrorMessageExtractor.extract(
                            response = response,
                            defaultMessage = "Не удалось загрузить профиль"
                        )
                    )
                }
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = ApiErrorMessageExtractor.extract(
                        exception = e,
                        defaultMessage = "Не удалось загрузить профиль",
                        unauthorizedMessage = "Сессия истекла. Войдите заново.",
                        forbiddenMessage = "Недостаточно прав для просмотра профиля"
                    )
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Не удалось загрузить профиль"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Не удалось загрузить профиль"
                )
            }
        }
    }

    fun changePassword() {
        val current = state.value.currentPassword.trim()
        val next = state.value.newPassword.trim()

        if (current.isBlank()) {
            _state.value = _state.value.copy(passwordChangeMessage = "Введите текущий пароль")
            return
        }
        if (next.length < 8) {
            _state.value = _state.value.copy(passwordChangeMessage = "Новый пароль должен быть не короче 8 символов")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(
                passwordChangeLoading = true,
                passwordChangeMessage = null
            )

            try {
                val response = ApiClient.authApi.changePassword(
                    ChangePasswordRequest(
                        currentPassword = current,
                        newPassword = next
                    )
                )

                _state.value = if (response.isSuccessful) {
                    _state.value.copy(
                        currentPassword = "",
                        newPassword = "",
                        passwordChangeLoading = false,
                        passwordChangeMessage = "Пароль изменён"
                    )
                } else {
                    _state.value.copy(
                        passwordChangeLoading = false,
                        passwordChangeMessage = ApiErrorMessageExtractor.extract(
                            response = response,
                            defaultMessage = "Не удалось изменить пароль"
                        )
                    )
                }
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    passwordChangeLoading = false,
                    passwordChangeMessage = ApiErrorMessageExtractor.extract(
                        exception = e,
                        defaultMessage = "Не удалось изменить пароль",
                        badRequestMessage = "Проверьте введённые пароли",
                        unauthorizedMessage = "Сессия истекла. Войдите заново.",
                        forbiddenMessage = "Недостаточно прав для смены пароля"
                    )
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(
                    passwordChangeLoading = false,
                    passwordChangeMessage = "Не удалось изменить пароль"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    passwordChangeLoading = false,
                    passwordChangeMessage = e.message ?: "Не удалось изменить пароль"
                )
            }
        }
    }

    private fun applyProfile(profile: AdminProfileResponse) {
        _state.value = _state.value.copy(
            name = profile.fullName?.takeIf { it.isNotBlank() }
                ?: profile.name?.takeIf { it.isNotBlank() }
                ?: _state.value.name,
            login = profile.username?.takeIf { it.isNotBlank() }
                ?: profile.login?.takeIf { it.isNotBlank() }
                ?: _state.value.login,
            role = profile.role?.takeIf { it.isNotBlank() } ?: _state.value.role,
            loading = false,
            error = null
        )
    }

    private suspend fun decodeProfileFromToken(): AdminProfileUiState {
        val token = tokenStorage.token.first()
        if (token.isNullOrBlank()) return AdminProfileUiState()

        val payload = token.split(".").getOrNull(1) ?: return AdminProfileUiState()
        return runCatching {
            val decoded = String(
                Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
            )
            val json = JsonParser.parseString(decoded).asJsonObject
            val login = firstString(json, "preferred_username", "username", "sub") ?: "Неизвестно"
            val name = firstString(json, "name", "fullName", "given_name") ?: "Администратор"
            val role = extractRole(json) ?: "Неизвестно"
            AdminProfileUiState(name = name, login = login, role = role)
        }.getOrDefault(AdminProfileUiState())
    }

    private fun firstString(json: JsonObject, vararg keys: String): String? {
        return keys.firstNotNullOfOrNull { key ->
            json.get(key)?.takeIf { it.isJsonPrimitive }?.asString?.takeIf { it.isNotBlank() }
        }
    }

    private fun extractRole(json: JsonObject): String? {
        val direct = firstString(json, "role", "authority")
        if (direct != null) return direct

        val rolesElement = json.get("roles")
        if (rolesElement != null && rolesElement.isJsonArray) {
            return rolesElement.asJsonArray.firstOrNull()?.asString
        }
        return null
    }
}
