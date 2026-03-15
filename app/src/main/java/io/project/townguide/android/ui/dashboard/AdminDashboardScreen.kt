package io.project.townguide.android.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

private data class DashboardPage(
    val title: String,
    val heroTitle: String,
    val heroDescription: String,
    val actions: List<DashboardAction>
)

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
    onProfileClick: () -> Unit
) {
    val pages = remember(
        onCitiesClick,
        onAddCityClick,
        onAddStoryClick,
        onAddPlaceClick,
        onAddPhotoClick
    ) {
        listOf(
            DashboardPage(
                title = "Города",
                heroTitle = "Управление городами",
                heroDescription = "Каталог городов и создание новых карточек для Telegram-бота.",
                actions = listOf(
                    DashboardAction("01", "Список городов", "Просмотр доступных городов, их историй и мест.", onCitiesClick),
                    DashboardAction("02", "Добавить город", "Создание нового города для контентной структуры.", onAddCityClick)
                )
            ),
            DashboardPage(
                title = "Истории",
                heroTitle = "Контентные истории",
                heroDescription = "Наполнение городов историями и текстовыми сюжетами.",
                actions = listOf(
                    DashboardAction("01", "Добавить историю", "Создание новой истории и привязка к выбранному городу.", onAddStoryClick)
                )
            ),
            DashboardPage(
                title = "Места",
                heroTitle = "Точки интереса",
                heroDescription = "Будущая секция для мест, маршрутов и географических привязок.",
                actions = listOf(
                    DashboardAction("01", "Добавить место", "Форма создания места и описания для выбранного города.", onAddPlaceClick)
                )
            ),
            DashboardPage(
                title = "Фото",
                heroTitle = "Медиаконтент",
                heroDescription = "Загрузка и связь фотографий с городами, местами и историями.",
                actions = listOf(
                    DashboardAction("01", "Добавить фото", "Управление визуальным контентом и вложениями.", onAddPhotoClick)
                )
            )
        )
    }

    val tabTitles = pages.map { it.title }
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    GlassScreen {
        GlassPanel {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                GlassSecondaryButton(
                    text = "Профиль",
                    onClick = onProfileClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            Text(
                text = "Панель администратора",
                style = MaterialTheme.typography.headlineLarge
            )

            Text(
                text = "Свайпайте между разделами или переключайтесь по вкладкам сверху.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    tabTitles.forEachIndexed { index, title ->
                        val selected = pagerState.currentPage == index
                        val modifier = if (selected) {
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                        } else {
                            Modifier
                        }

                        GlassChip(
                            text = title,
                            modifier = modifier.clickable {
                                scope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )
                    }
                }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        ) { page ->
            DashboardSectionPage(page = pages[page])
        }
    }
}

@Composable
private fun DashboardSectionPage(page: DashboardPage) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        GlassPanel {
            Text(
                text = page.heroTitle,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = page.heroDescription,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 220.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(page.actions) { action ->
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
