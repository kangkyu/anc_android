package com.example.ancandroid.views

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.example.ancandroid.R
import com.example.ancandroid.navigation.Screens


@Composable
fun ChurchBottomNavigationBar(
    onSermonsClicked: () -> Unit, onHomeClicked: () -> Unit, onJuboClicked: () -> Unit
) {
    val items = listOf(Screens.SermonVideosScreen, Screens.JuboImagesScreen, Screens.HomeScreen)
    val selectedItem = remember { mutableStateOf(items[0]) }
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = selectedItem.value == Screens.SermonVideosScreen,
            onClick = {
                onSermonsClicked()
                selectedItem.value = Screens.SermonVideosScreen
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sermons_selected),
                    contentDescription = "Sermons Icon"
                )
            },
            label = {
                Text("설교", textAlign = TextAlign.Center)
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
                    painter = painterResource(id = R.drawable.ic_jubo_selected),
                    contentDescription = "Jubo Icon"
                )
            },
            label = {
                Text("주보", textAlign = TextAlign.Center)
            }
        )

        NavigationBarItem(
            selected = selectedItem.value == Screens.HomeScreen,
            onClick = {
                onHomeClicked()
                selectedItem.value = Screens.HomeScreen
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_info_selected),
                    contentDescription = "Info Icon"
                )
            },
            label = {
                Text("안내", textAlign = TextAlign.Center)
            }
        )
    }
}
