package com.anconnuri.ancandroid.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object SermonVideosScreen : Screens("sermons")
    object JuboImagesScreen : Screens("jubo")
    object PrayerScreen : Screens("prayer")
    object LoginScreen : Screens("login")
}
