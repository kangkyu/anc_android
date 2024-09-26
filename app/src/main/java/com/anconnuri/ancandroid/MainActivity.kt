package com.anconnuri.ancandroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.anconnuri.ancandroid.ui.theme.ANCAndroidTheme
import androidx.navigation.compose.rememberNavController
import com.anconnuri.ancandroid.navigation.AppNavigation
import com.anconnuri.ancandroid.navigation.Screens
import com.anconnuri.ancandroid.navigation.ChurchBottomNavigationBar
import com.google.firebase.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.appcheck.appCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.initialize

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
                        CenterAlignedTopAppBar(
                            title = {
                                Icon(
                                    painter = painterResource(id = R.drawable.logo_png),
                                    contentDescription = "ANC Onnuri",
                                    tint = Color.Unspecified,
                                    modifier = Modifier.width(80.dp))
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
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
                            },
                            onPrayerClicked = {
                                navController.navigate(Screens.PrayerScreen.route)
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

//        initDebug()
        init()
    }

    private fun init() {
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )
    }

    private fun initDebug() {
        Firebase.initialize(context = this)
        Firebase.appCheck.installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance(),
        )
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
