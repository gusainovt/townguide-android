package io.project.townguide.android.ui.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.project.townguide.android.data.storage.TokenStorage

class AdminLoginViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdminLoginViewModel::class.java)) {
            return AdminLoginViewModel(
                TokenStorage(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel")
    }
}