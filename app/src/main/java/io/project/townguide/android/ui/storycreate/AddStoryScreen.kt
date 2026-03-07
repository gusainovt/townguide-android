package io.project.townguide.android.ui.storycreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.project.townguide.android.data.network.dto.CityResponse
import io.project.townguide.android.ui.components.GlassButton
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassMessage
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton
import io.project.townguide.android.ui.components.GlassTextField

@Composable
fun AddStoryScreen(
    onBack: () -> Unit
) {
    val viewModel: AddStoryViewModel = viewModel()
    val cities by viewModel.cities.collectAsState()
    val loadingCities by viewModel.loadingCities.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val body by viewModel.body.collectAsState()
    val submitting by viewModel.submitting.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

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
                        text = "Добавление истории",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    Text(
                        text = "Сначала выберите город, затем отправьте текст истории.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )

                    GlassChip(
                        text = selectedCity?.let { "Город: ${it.name}" } ?: "Шаг 1: выбор города"
                    )
                }
            }

            if (selectedCity == null) {
                item {
                    CitySelectionPanel(
                        cities = cities,
                        loading = loadingCities,
                        error = error,
                        onRetry = viewModel::loadCities,
                        onCityClick = viewModel::selectCity
                    )
                }
            } else {
                item {
                    GlassPanel {
                        GlassSecondaryButton(
                            text = "К выбору городов",
                            onClick = viewModel::backToCitySelection,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Text(
                            text = "Новая история для ${selectedCity!!.name}",
                            style = MaterialTheme.typography.titleLarge
                        )

                        GlassTextField(
                            value = body,
                            onValueChange = viewModel::onBodyChanged,
                            label = "Текст истории",
                            modifier = Modifier.heightIn(min = 180.dp),
                            enabled = !submitting,
                            singleLine = false
                        )

                        if (error != null) {
                            GlassMessage(text = error!!)
                        }

                        if (successMessage != null) {
                            GlassMessage(
                                text = successMessage!!,
                                accentColor = MaterialTheme.colorScheme.primary
                            )
                        }

                        GlassButton(
                            text = if (submitting) "Сохраняем историю" else "Создать историю",
                            onClick = viewModel::onCreateStoryClick,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !submitting,
                            loading = submitting
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CitySelectionPanel(
    cities: List<CityResponse>,
    loading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onCityClick: (CityResponse) -> Unit
) {
    GlassPanel {
        Text(
            text = "Выберите город",
            style = MaterialTheme.typography.titleLarge
        )

        when {
            loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "Загружаем города",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            error != null && cities.isEmpty() -> {
                GlassMessage(text = error)
                GlassButton(
                    text = "Повторить",
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            cities.isEmpty() -> {
                GlassMessage(
                    text = "Список городов пуст",
                    accentColor = Color(0xFF956B00)
                )
            }

            else -> {
                cities.forEach { city ->
                    GlassButton(
                        text = city.name,
                        onClick = { onCityClick(city) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
