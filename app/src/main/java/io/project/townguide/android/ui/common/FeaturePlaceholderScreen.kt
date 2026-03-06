package io.project.townguide.android.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.project.townguide.android.ui.components.GlassButton
import io.project.townguide.android.ui.components.GlassChip
import io.project.townguide.android.ui.components.GlassPanel
import io.project.townguide.android.ui.components.GlassScreen
import io.project.townguide.android.ui.components.GlassSecondaryButton

@Composable
fun FeaturePlaceholderScreen(
    title: String,
    description: String,
    onBack: () -> Unit
) {
    GlassScreen {
        GlassSecondaryButton(
            text = "Назад",
            onClick = onBack,
            modifier = Modifier.align(Alignment.Start)
        )

        GlassPanel(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true),
            contentPadding = PaddingValues(28.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                GlassChip(text = "В разработке")

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )

                GlassButton(
                    text = "Вернуться в панель",
                    onClick = onBack
                )
            }
        }
    }
}
