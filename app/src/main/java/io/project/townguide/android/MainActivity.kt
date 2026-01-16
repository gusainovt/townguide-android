package io.project.townguide.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.project.townguide.android.data.storage.TokenStorage
import io.project.townguide.android.ui.login.AdminLoginScreen
import io.project.townguide.android.ui.main.MainScreen
import io.project.townguide.android.ui.navigation.Routes
import io.project.townguide.android.ui.splash.SplashScreen
import io.project.townguide.android.ui.splash.SplashViewModel
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
                        SplashScreen()

                        SplashViewModel(
                            tokenStorage = TokenStorage(TownguideApp.appContext)
                        ) { hasToken ->
                            if (hasToken) {
                                navController.navigate(Routes.MAIN) {
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
                                navController.navigate(Routes.MAIN) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Routes.MAIN) {
                        MainScreen()
                    }
                }
            }
        }
    }
}