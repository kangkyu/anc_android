package com.example.ancandroid.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.ancandroid.navigation.Screens


@Composable
fun ChurchBottomNavigationBar(
    onSermonsClicked: () -> Unit, onHomeClicked: () -> Unit, onJuboClicked: () -> Unit
) {
    val items = listOf(Screens.HomeScreen, Screens.SermonVideosScreen)
    val selectedItem = remember { mutableStateOf(items[0]) }
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = selectedItem.value == Screens.HomeScreen,
            onClick = {
                onHomeClicked()
                selectedItem.value = Screens.HomeScreen
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home Icon"
                )
            }
        )

        NavigationBarItem(
            selected = selectedItem.value == Screens.SermonVideosScreen,
            onClick = {
                onSermonsClicked()
                selectedItem.value = Screens.SermonVideosScreen
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Favorite Icon"
                )
            }
        )

        NavigationBarItem(
            selected = selectedItem.value == Screens.JuboImagesScreen,
            onClick = {
                onJuboClicked()
                selectedItem.value = Screens.JuboImagesScreen
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Account Icon"
                )
            }
        )
    }
}
