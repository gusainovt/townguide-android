package io.project.townguide.android.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton

private data class DashboardAction(
    val index: String,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

@Composable
fun AdminDashboardScreen(
    onCitiesClick: () -> Unit,
    onAddCityClick: () -> Unit,
    onAddStoryClick: () -> Unit,
    onAddPlaceClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val actions = listOf(
        DashboardAction(
            index = "01",
            title = "Города",
            description = "Просмотр списка городов, историй и мест.",
            onClick = onCitiesClick
        ),
        DashboardAction(
            index = "02",
            title = "Новый город",
            description = "Форма создания нового города для бота.",
            onClick = onAddCityClick
        ),
        DashboardAction(
            index = "03",
            title = "История",
            description = "Добавление контентных историй к городу.",
            onClick = onAddStoryClick
        ),
        DashboardAction(
            index = "04",
            title = "Место",
            description = "Создание точек интереса и описаний.",
            onClick = onAddPlaceClick
        ),
        DashboardAction(
            index = "05",
            title = "Фото",
            description = "Управление медиа и изображениями.",
            onClick = onAddPhotoClick
        )
    )

    GlassScreen {
        GlassPanel {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Админ-сервис Townguide",
                    style = MaterialTheme.typography.headlineLarge
                )

                GlassSecondaryButton(
                    text = "Выйти",
                    onClick = onLogoutClick
                )
            }

            Text(
                text = "Единая панель для администрирования Telegram-бота и структуры туристического контента.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassChip(text = "${actions.size} разделов")
                GlassChip(text = "Готово: вход и города")
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 170.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(actions) { action ->
                DashboardActionCard(action = action)
            }
        }
    }
}

@Composable
private fun DashboardActionCard(action: DashboardAction) {
    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .clickable(onClick = action.onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.secondary
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = action.index,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Text(
            text = action.title,
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = action.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Text(
            text = "Открыть раздел",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}
