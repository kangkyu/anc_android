package com.anconnuri.ancandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import com.anconnuri.ancandroid.views.ChurchInfoView
import com.anconnuri.ancandroid.views.JuboView
import com.anconnuri.ancandroid.views.PhoneAuthScreen
import com.anconnuri.ancandroid.views.PrayerView
import com.anconnuri.ancandroid.views.SermonVideosView
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import org.koin.core.qualifier.named

@Composable
fun AppNavigation(
    navHostController: NavHostController = rememberNavController()
) {
    val mainGraphScope = getKoin().createScope(Screens.PrayerScreen.route, named("main_graph_scope"))

    val scope = getKoin().getScope(Screens.PrayerScreen.route)
    val authViewModel: PhoneAuthViewModel by scope.inject()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navHostController,
        startDestination = Screens.SermonVideosScreen.route
    ) {
        composable(Screens.SermonVideosScreen.route) {
            SermonVideosView()
        }
        composable(Screens.JuboImagesScreen.route) {
            JuboView()
        }
        composable(Screens.HomeScreen.route) {
            ChurchInfoView()
        }
        composable(Screens.PrayerScreen.route) {
            ProtectedRoute(
                isLoggedIn = isLoggedIn,
                onLoginRequired = {
                    navHostController.navigate(Screens.LoginScreen.route)
                }
            ) {
                PrayerView()
            }
        }
        composable(Screens.LoginScreen.route) {
            PhoneAuthScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    // TODO: redirect to target screen
                    navHostController.navigate(Screens.PrayerScreen.route)
                }
            )
        }
    }
}
