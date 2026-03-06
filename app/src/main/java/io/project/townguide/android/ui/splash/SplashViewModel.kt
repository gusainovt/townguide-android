package io.project.townguide.android.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.storage.TokenStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenStorage: TokenStorage,
) : ViewModel() {

    private val _hasToken = MutableStateFlow<Boolean?>(null)
    val hasToken = _hasToken.asStateFlow()

    init {
        viewModelScope.launch {
            val token = tokenStorage.token.first()
            _hasToken.value = token?.isNotBlank() == true
        }
    }
}
