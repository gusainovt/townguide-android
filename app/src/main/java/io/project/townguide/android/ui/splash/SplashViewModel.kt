package io.project.townguide.android.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.storage.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashViewModel(
    private val tokenStorage: TokenStorage,
    private val onResult: (Boolean) -> Unit
) : ViewModel() {

    init {
        viewModelScope.launch {
            val tokenExists = tokenStorage.token.first() != null
            onResult(tokenExists)
        }
    }
}