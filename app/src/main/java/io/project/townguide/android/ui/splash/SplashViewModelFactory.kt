package io.project.townguide.android.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.project.townguide.android.data.storage.TokenStorage

class SplashViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(
                TokenStorage(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}