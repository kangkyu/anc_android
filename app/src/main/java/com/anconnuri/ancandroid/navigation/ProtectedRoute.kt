package com.anconnuri.ancandroid.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProtectedRoute(
    isAuthenticated: Boolean,
    onLoginRequired: () -> Unit,
    content: @Composable () -> Unit
) {
    if (isAuthenticated) {
        content()
    } else {
        DisposableEffect(Unit) {
            onLoginRequired()
            onDispose { }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
