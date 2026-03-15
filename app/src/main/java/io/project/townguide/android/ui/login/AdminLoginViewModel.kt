package io.project.townguide.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.ApiErrorMessageExtractor
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.LoginRequest
import io.project.townguide.android.data.storage.TokenStorage
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AdminLoginViewModel(
    private val tokenStorage: TokenStorage
) : ViewModel() {
    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _login = MutableStateFlow("")
    val login: StateFlow<String> = _login

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun onLoginChanged(value: String) {
        _login.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = ApiClient.authApi.login(
                    LoginRequest(
                        username = login.value,
                        password = password.value
                    )
                )

                tokenStorage.saveTokens(response.token, response.refreshToken)
                _loginSuccess.value = true
            } catch (e: HttpException) {
                _error.value = ApiErrorMessageExtractor.extract(
                    exception = e,
                    defaultMessage = "Ошибка сервера",
                    unauthorizedMessage = "Неверный логин или пароль",
                    forbiddenMessage = "Неверный логин, пароль или недостаточно прав"
                )
            } catch (e: IOException) {
                _error.value = ApiErrorMessageExtractor.extract(
                    exception = e,
                    defaultMessage = "Нет соединения с backend API. Проверьте dev-сервер на 8080 и адрес хоста."
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Не удалось выполнить вход"
            } finally {
                _loading.value = false
            }
        }
    }
}
