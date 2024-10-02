package com.anconnuri.ancandroid.navigation

sealed class Screens(val route: String) {
    object HomeScreen : Screens("home")
    object SermonVideosScreen : Screens("sermons")
    object JuboImagesScreen : Screens("jubo")
    object PrayerScreen : Screens("prayer")
    object AddPrayerScreen : Screens("add_prayer_request")
    object LoginScreen : Screens("login")
}
