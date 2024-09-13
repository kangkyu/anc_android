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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.anconnuri.ancandroid.ui.theme.ANCAndroidTheme
import androidx.navigation.compose.rememberNavController
import com.anconnuri.ancandroid.navigation.AppNavigation
import com.anconnuri.ancandroid.navigation.Screens
import com.anconnuri.ancandroid.navigation.ChurchBottomNavigationBar
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.auth
import java.util.concurrent.TimeUnit
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.functions
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var functions: FirebaseFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        functions = Firebase.functions
        functions.useEmulator("127.0.0.1", 9099)

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
    }

    fun openBrowser(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    companion object {
        var instance: MainActivity? = null
            private set
    }

    fun phoneVerify() {
        // [START auth_test_phone_verify]
        val phoneNum = "+13239194452"

        // Whenever verification is triggered with the whitelisted number,
        // provided it is not set for auto-retrieval, onCodeSent will be triggered.
        val options = PhoneAuthOptions.newBuilder(Firebase.auth)
            .setPhoneNumber(phoneNum)
            .setTimeout(30L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onCodeSent(
                    verificationId: String,
                    forceResendingToken: PhoneAuthProvider.ForceResendingToken,
                ) {
                    // Save the verification id somewhere
                    // ...

                    // The corresponding whitelisted code above should be used to complete sign-in.
                    this@MainActivity.enableUserManuallyInputCode()
                }

                override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                    // Sign in with the credential
                    // ...
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    // ...
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END auth_test_phone_verify]
    }

    private fun enableUserManuallyInputCode() {
        // No-op
    }

    fun phoneAutoRetrieve() {
        // [START auth_test_phone_auto]
        // The test phone number and code should be whitelisted in the console.
        val phoneNumber = "+13239194452"
        val smsCode = "123456"

        val firebaseAuth = Firebase.auth
        val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings

        // Configure faking the auto-retrieval with the whitelisted numbers.
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneNumber, smsCode)

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Instant verification is applied and a credential is directly returned.
                    // ...
                }

                // [START_EXCLUDE]
                override fun onVerificationFailed(e: FirebaseException) {
                }
                // [END_EXCLUDE]
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
        // [END auth_test_phone_auto]
    }
}

fun openUrlInExternalBrowser(url: String) {
    MainActivity.instance?.openBrowser(url)
}
