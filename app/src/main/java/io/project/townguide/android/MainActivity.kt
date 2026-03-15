package io.project.townguide.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.project.townguide.android.data.session.SessionEvents
import io.project.townguide.android.data.storage.TokenStorage
import io.project.townguide.android.ui.cities.CitiesScreen
import io.project.townguide.android.ui.citycreate.AddCityScreen
import io.project.townguide.android.ui.common.FeaturePlaceholderScreen
import io.project.townguide.android.ui.dashboard.AdminDashboardScreen
import io.project.townguide.android.ui.dashboard.AdminProfileScreen
import io.project.townguide.android.ui.login.AdminLoginScreen
import io.project.townguide.android.ui.navigation.Routes
import io.project.townguide.android.ui.splash.SplashScreen
import io.project.townguide.android.ui.splash.SplashViewModel
import io.project.townguide.android.ui.splash.SplashViewModelFactory
import io.project.townguide.android.ui.storycreate.AddStoryScreen
import io.project.townguide.android.ui.theme.TownguideTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TownguideTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

                NavHost(
                    navController = navController,
                    startDestination = Routes.SPLASH
                ) {
                    composable(Routes.SPLASH) {
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
                            onAddPhotoClick = { navController.navigate(Routes.ADD_PHOTO) },
                            onProfileClick = { navController.navigate(Routes.PROFILE) }
                        )
                    }

                    composable(Routes.PROFILE) {
                        AdminProfileScreen(
                            onBack = { navController.navigateUp() },
                            onLogoutClick = {
                                scope.launch {
                                    TokenStorage(context).clear()
                                    SessionEvents.notifySessionExpired()
                                }
                            }
                        )
                    }

                    composable(Routes.CITIES) {
                        CitiesScreen(
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_CITY) {
                        AddCityScreen(
                            onBack = { navController.navigateUp() },
                            onCityCreated = {
                                navController.navigate(Routes.CITIES) {
                                    popUpTo(Routes.ADD_CITY) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.ADD_STORY) {
                        AddStoryScreen(
                            onBack = { navController.navigateUp() }
                        )
                    }

                    composable(Routes.ADD_PLACE) {
                        FeaturePlaceholderScreen(
                            title = "Добавление места",
                            description = "Раздел под создание точки интереса подготовлен, но форма еще не сделана. Логичный следующий шаг: название, описание, координаты и связь с городом.",
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

                LaunchedEffect(Unit) {
                    SessionEvents.sessionExpired.collect {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
}
