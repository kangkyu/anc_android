package com.example.ancandroid.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object SermonVideosScreen : Screens("sermons")
}
