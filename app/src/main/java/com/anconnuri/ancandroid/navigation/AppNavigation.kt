package com.anconnuri.ancandroid.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.anconnuri.ancandroid.views.ChurchInfoView
import com.anconnuri.ancandroid.views.JuboView
import com.anconnuri.ancandroid.views.SermonVideosView

@Composable
fun AppNavigation(navHostController: NavHostController = rememberNavController()) {
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
    }
}
