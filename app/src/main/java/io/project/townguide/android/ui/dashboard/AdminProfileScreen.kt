package io.project.townguide.android.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.project.townguide.android.ui.components.GlassButton
import io.project.townguide.android.ui.components.GlassMessage
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton
import io.project.townguide.android.ui.components.GlassTextField

@Composable
fun AdminProfileScreen(
    onBack: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: AdminProfileViewModel = viewModel(
        factory = AdminProfileViewModelFactory(context)
    )
    val state by viewModel.state.collectAsState()

    GlassScreen {
        GlassSecondaryButton(
            text = "Назад",
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            item {
                GlassPanel {
                    Text(
                        text = "Профиль администратора",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Просмотр данных аккаунта, смена пароля и завершение сессии.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )
                }
            }

            item {
                GlassPanel {
                    if (state.loading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text("Загружаем профиль")
                        }
                    }

                    ProfileField("Имя", state.name)
                    ProfileField("Логин", state.login)
                    ProfileField("Роль", state.role)

                    if (state.error != null) {
                        GlassMessage(text = state.error!!)
                    }
                }
            }

            item {
                GlassPanel {
                    Text(
                        text = "Смена пароля",
                        style = MaterialTheme.typography.titleLarge
                    )

                    GlassTextField(
                        value = state.currentPassword,
                        onValueChange = viewModel::onCurrentPasswordChanged,
                        label = "Текущий пароль",
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !state.passwordChangeLoading
                    )

                    GlassTextField(
                        value = state.newPassword,
                        onValueChange = viewModel::onNewPasswordChanged,
                        label = "Новый пароль",
                        visualTransformation = PasswordVisualTransformation(),
                        enabled = !state.passwordChangeLoading
                    )

                    if (state.passwordChangeMessage != null) {
                        GlassMessage(
                            text = state.passwordChangeMessage!!,
                            accentColor = if (state.passwordChangeMessage == "Пароль изменён") {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.error
                            }
                        )
                    }

                    GlassButton(
                        text = if (state.passwordChangeLoading) "Сохраняем пароль" else "Сменить пароль",
                        onClick = viewModel::changePassword,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !state.passwordChangeLoading,
                        loading = state.passwordChangeLoading
                    )
                }
            }

            item {
                GlassSecondaryButton(
                    text = "Выйти из аккаунта",
                    onClick = onLogoutClick
                )
            }
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
