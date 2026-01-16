package io.project.townguide.android.ui.login

import retrofit2.HttpException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.ApiClient
import io.project.townguide.android.data.network.dto.LoginRequest
import io.project.townguide.android.data.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
                tokenStorage.saveToken(response.token)
                _loginSuccess.value = true
            } catch (e: HttpException) {
                _error.value = when (e.code()) {
                    401, 403 -> "Неверный логин или пароль"
                    else -> "Ошибка сервера (${e.code()})"
                }
            } catch (e: Exception) {
                println("Login error: ${e.message}")
            } finally {
            _loading.value = false
            }
        }
    }
}