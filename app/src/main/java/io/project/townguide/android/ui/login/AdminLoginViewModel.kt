package io.project.townguide.android.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.ApiClient
import io.project.townguide.android.data.network.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AdminLoginViewModel : ViewModel() {

    private val _login = MutableStateFlow("")
    val login: StateFlow<String> = _login

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    fun onLoginChanged(value: String) {
        _login.value = value
    }

    fun onPasswordChanged(value: String) {
        _password.value = value
    }

    fun onLoginClick() {
        viewModelScope.launch {
            try {
                val response = ApiClient.authApi.login(
                    LoginRequest(
                        username = login.value,
                        password = password.value
                    )
                )
                println("JWT: ${response.token}")
            } catch (e: Exception) {
                println("Login error: ${e.message}")
            }
        }
    }
}