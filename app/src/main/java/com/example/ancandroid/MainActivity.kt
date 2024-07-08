package com.example.ancandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.ancandroid.ui.theme.ANCAndroidTheme
import androidx.navigation.compose.rememberNavController
import com.example.ancandroid.navigation.AppNavigation
import com.example.ancandroid.navigation.Screens
import com.example.ancandroid.views.ChurchBottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            ANCAndroidTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(R.string.app_title))
                            },
                            colors =  TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                            )
                        )
                    },
                    bottomBar = {
                        ChurchBottomNavigationBar(
                            onSermonsClicked = {
                                navController.navigate(Screens.SermonVideosScreen.route)
                            },
                            onHomeClicked = {
                                navController.navigate(Screens.HomeScreen.route)
                            },
                            onJuboClicked = {
                                navController.navigate(Screens.JuboImagesScreen.route)
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        AppNavigation(
                            navHostController = navController
                        )
                    }
                }
            }
        }
    }

    fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        var instance: MainActivity? = null
            private set
    }
}

fun openUrlInExternalBrowser(url: String) {
    MainActivity.instance?.openBrowser(url)
}
