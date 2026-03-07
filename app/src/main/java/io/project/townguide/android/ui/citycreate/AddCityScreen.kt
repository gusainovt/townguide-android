package io.project.townguide.android.ui.citycreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.project.townguide.android.ui.components.GlassButton
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassMessage
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton
import io.project.townguide.android.ui.components.GlassTextField

@Composable
fun AddCityScreen(
    onBack: () -> Unit,
    onCityCreated: () -> Unit
) {
    val viewModel: AddCityViewModel = viewModel()
    val name by viewModel.name.collectAsState()
    val nameEng by viewModel.nameEng.collectAsState()
    val description by viewModel.description.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val creationCompleted by viewModel.creationCompleted.collectAsState()

    LaunchedEffect(creationCompleted) {
        if (creationCompleted) {
            onCityCreated()
        }
    }

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
                        text = "Добавление города",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Форма отправляет POST /api/v1/city и после успешного создания открывает список городов.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )

                    GlassChip(text = "3 поля")
                }
            }

            item {
                GlassPanel {
                    GlassTextField(
                        value = name,
                        onValueChange = viewModel::onNameChanged,
                        label = "Название города",
                        enabled = !loading
                    )

                    GlassTextField(
                        value = nameEng,
                        onValueChange = viewModel::onNameEngChanged,
                        label = "Название латиницей",
                        enabled = !loading
                    )

                    GlassTextField(
                        value = description,
                        onValueChange = viewModel::onDescriptionChanged,
                        label = "Описание",
                        modifier = Modifier.heightIn(min = 140.dp),
                        enabled = !loading,
                        singleLine = false
                    )

                    if (error != null) {
                        GlassMessage(text = error!!)
                    }

                    GlassButton(
                        text = if (loading) "Создаём город" else "Создать город",
                        onClick = viewModel::onCreateClick,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !loading,
                        loading = loading
                    )
                }
            }
        }
    }
}
