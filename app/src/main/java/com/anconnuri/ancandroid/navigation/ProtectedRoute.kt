package com.anconnuri.ancandroid.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect

@Composable
fun ProtectedRoute(
    isLoggedIn: Boolean,
    onLoginRequired: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isLoggedIn) {
        content()
    } else {
        DisposableEffect(Unit) {
            onLoginRequired()
            onDispose { }
        }
        // You can show a loading indicator here if needed
    }
}