package io.project.townguide.android.ui.cities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassMessage
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton

@Composable
fun CitiesScreen(
    onBack: () -> Unit
) {
    val viewModel: CitiesViewModel = viewModel()
    val cities by viewModel.cities.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    GlassScreen {
        GlassSecondaryButton(
            text = "Назад",
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        )

        GlassPanel {
            Text(
                text = "Города",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Каталог городов, доступных в Telegram-боте, с вложенными местами и историями.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )

            GlassChip(text = "${cities.size} элементов")
        }

        when {
            loading -> {
                GlassPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
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
            }

            error != null -> {
                GlassPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlassMessage(text = error!!)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = true),
                    contentPadding = PaddingValues(bottom = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(cities, key = { it.id }) { city ->
                        CityCard(city = city)
                    }
                }
            }
        }
    }
}
