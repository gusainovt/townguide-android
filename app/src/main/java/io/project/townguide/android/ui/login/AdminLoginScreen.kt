package io.project.townguide.android.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.project.townguide.android.ui.components.GlassButton
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassMessage
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassTextField
import io.project.townguide.android.ui.components.LiquidGlassBackground

@Composable
fun AdminLoginScreen(
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: AdminLoginViewModel = viewModel(
        factory = AdminLoginViewModelFactory(context)
    )

    val login by viewModel.login.collectAsState()
    val password by viewModel.password.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
        }
    }

    LiquidGlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.safeDrawing.asPaddingValues())
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 540.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                GlassChip(text = "Townguide")

                GlassPanel(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(28.dp)
                ) {
                    Text(
                        text = "Панель администратора",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Вход для управления Telegram-ботом, городами и медиаконтентом.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )

                    GlassTextField(
                        value = login,
                        onValueChange = viewModel::onLoginChanged,
                        label = "Логин",
                        enabled = !loading
                    )

                    GlassTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChanged,
                        label = "Пароль",
                        enabled = !loading,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    if (error != null) {
                        GlassMessage(text = error!!)
                    }

                    GlassButton(
                        text = if (loading) "Выполняем вход" else "Войти",
                        onClick = viewModel::onLoginClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        loading = loading
                    )

                    Text(
                        text = "Используйте учетную запись администратора API.",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
                    )
                }
            }
        }
    }
}
