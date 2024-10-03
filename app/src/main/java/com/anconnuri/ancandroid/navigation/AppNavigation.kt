package com.anconnuri.ancandroid.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import com.anconnuri.ancandroid.views.AddPrayerRequestScreen
import com.anconnuri.ancandroid.views.ChurchInfoView
import com.anconnuri.ancandroid.views.JuboView
import com.anconnuri.ancandroid.views.PhoneAuthScreen
import com.anconnuri.ancandroid.views.PrayerScreen
import com.anconnuri.ancandroid.views.SermonVideosView
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppNavigation(
    navHostController: NavHostController = rememberNavController()
) {
    val authViewModel: PhoneAuthViewModel = koinViewModel()
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
                    navHostController.navigate(Screens.LoginScreen.route) {
                        popUpTo(Screens.PrayerScreen.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                PrayerScreen(
                    onAddPrayer = {
                        navHostController.navigate(Screens.AddPrayerScreen.route)
                    },
                    onSignOut = {
                        authViewModel.signOut()
                        navHostController.navigate(Screens.LoginScreen.route)
                    }
                )
            }
        }
        composable(Screens.AddPrayerScreen.route) {
            AddPrayerRequestScreen(navHostController)
        }
        composable(Screens.LoginScreen.route) {
            PhoneAuthScreen(
                viewModel = authViewModel,
                onLoginSuccess = { user ->
                    user!!.getIdToken(true).addOnCompleteListener { task ->

                        if (task.isSuccessful) {
                            val idToken: String = task.result.token.toString()
                            // Send token to your backend via HTTPS
                            // TODO: Fix it not to be triggered multiple times
                            authViewModel.sendIdTokenToServer(idToken)
                        } else {
                            Log.d("IdToken", "getIdToken not successful")
                            // Handle error -> task.getException();
                        }
                    }
                    // Redirect to target screen
                    navHostController.navigate(Screens.PrayerScreen.route)
                }
            )
        }
    }
}
