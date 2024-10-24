package com.anconnuri.ancandroid.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
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
        navigation(
            startDestination = Screens.LoginScreen.route,
            route = "auth"
        ) {
            composable(Screens.LoginScreen.route) {
                val authViewModel: PhoneAuthViewModel = koinViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val tokenSent by authViewModel.tokenSent.collectAsState()

                LaunchedEffect(isLoggedIn, tokenSent) {
                    if (isLoggedIn && tokenSent) {
                        navHostController.navigate(Screens.PrayerScreen.route) {
                            popUpTo(Screens.LoginScreen.route) { inclusive = true }
                        }
                    }
                }

                PhoneAuthScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { user ->
                        user?.getIdToken(true)?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val idToken: String = task.result.token.toString()
                                authViewModel.sendIdTokenToServer(idToken)
                            } else {
                                Log.d("IdToken", "getIdToken not successful")
                            }
                        }
                    }
                )
            }

            composable(Screens.PrayerScreen.route) {
                val authViewModel: PhoneAuthViewModel = koinViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val tokenSent by authViewModel.tokenSent.collectAsState()

                ProtectedRoute(
                    isAuthenticated = isLoggedIn && tokenSent,
                    onLoginRequired = {
                        navHostController.navigate(Screens.LoginScreen.route) {
                            popUpTo("auth") { inclusive = false }
                        }
                    }
                ) {
                    PrayerScreen(
                        onAddPrayer = {
                            navHostController.navigate(Screens.AddPrayerScreen.route)
                        },
                        onSignOut = {
                            authViewModel.signOut()
                            navHostController.navigate(Screens.LoginScreen.route) {
                                popUpTo("auth") { inclusive = false }
                            }
                        }
                    )
                }
            }

            composable(Screens.AddPrayerScreen.route) {
                val authViewModel : PhoneAuthViewModel = koinViewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val tokenSent by authViewModel.tokenSent.collectAsState()

                ProtectedRoute(
                    isAuthenticated = isLoggedIn && tokenSent,
                    onLoginRequired = {
                        navHostController.navigate(Screens.LoginScreen.route) {
                            popUpTo("auth") { inclusive = false }
                        }
                    }
                ) {
                    AddPrayerRequestScreen(navHostController)
                }
            }
        }
    }
}
