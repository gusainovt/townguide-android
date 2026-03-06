package io.project.townguide.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.project.townguide.android.ui.cities.CitiesScreen
import io.project.townguide.android.ui.common.FeaturePlaceholderScreen
import io.project.townguide.android.ui.dashboard.AdminDashboardScreen
import io.project.townguide.android.ui.login.AdminLoginScreen
import io.project.townguide.android.ui.navigation.Routes
import io.project.townguide.android.ui.splash.SplashScreen
import io.project.townguide.android.ui.splash.SplashViewModel
import io.project.townguide.android.ui.splash.SplashViewModelFactory
import io.project.townguide.android.ui.theme.TownguideTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TownguideTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH
                ) {
                    composable(Routes.SPLASH) {
                        val context = LocalContext.current
                        val viewModel: SplashViewModel = viewModel(
                            factory = SplashViewModelFactory(context)
                        )

                        val hasToken by viewModel.hasToken.collectAsState()

                        SplashScreen()

                        LaunchedEffect(hasToken) {
                            val resolvedToken = hasToken ?: return@LaunchedEffect

                            if (resolvedToken) {
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }
                        }
                    }

                    composable(Routes.LOGIN) {
                        AdminLoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.DASHBOARD) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.DASHBOARD) {
                        AdminDashboardScreen(
                            onCitiesClick = { navController.navigate(Routes.CITIES) },
                            onAddCityClick = { navController.navigate(Routes.ADD_CITY) },
                            onAddStoryClick = { navController.navigate(Routes.ADD_STORY) },
                            onAddPlaceClick = { navController.navigate(Routes.ADD_PLACE) },
                            onAddPhotoClick = { navController.navigate(Routes.ADD_PHOTO) }
                        )
                    }

                    composable(Routes.CITIES) {
                        CitiesScreen(
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_CITY) {
                        FeaturePlaceholderScreen(
                            title = "Добавление города",
                            description = "Экран создания города еще не реализован. Следующим шагом можно собрать форму с названием, английским именем, callback-кодом, описанием и фото.",
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_STORY) {
                        FeaturePlaceholderScreen(
                            title = "Добавление истории",
                            description = "Пока доступна только навигационная точка. Следующим этапом стоит собрать форму истории с привязкой к выбранному городу.",
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_PLACE) {
                        FeaturePlaceholderScreen(
                            title = "Добавление места",
                            description = "Раздел под создание точки интереса подготовлен, но форма еще не сделана. Логичный следующий шаг — название, описание, координаты и связь с городом.",
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_PHOTO) {
                        FeaturePlaceholderScreen(
                            title = "Добавление фотографии",
                            description = "Маршрут создан как безопасная заглушка. Дальше сюда можно добавить загрузку изображения и привязку к городу, месту или истории.",
                            onBack = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}
