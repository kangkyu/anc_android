package com.example.ancandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ancandroid.views.ChurchInfoView
import com.example.ancandroid.views.SermonVideosView

@Composable
fun AppNavigation(navHostController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navHostController,
        startDestination = Screens.HomeScreen.route
    ) {
        composable(Screens.HomeScreen.route) {
            ChurchInfoView()
        }
        composable(Screens.SermonVideosScreen.route) {
            SermonVideosView()
        }
    }
}
