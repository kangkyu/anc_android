package com.anconnuri.ancandroid.viewmodel


import android.content.Context
import android.util.Base64
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.LoginResult
import com.anconnuri.ancandroid.utils.TokenManager
import com.anconnuri.ancandroid.views.AuthState
import com.google.android.gms.tasks.Task
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import com.google.firebase.Firebase
import io.ktor.util.encodeBase64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.IOException
import java.security.SecureRandom

const val YOUR_SERVER_URL = "https://anc-backend-7502ef948715.herokuapp.com/auth/firebase-auth"
const val YOUR_PROJECT_NUMBER = 684076341065

class PhoneAuthViewModel : ViewModel(), KoinComponent {
    private val applicationContext by inject<Context>()

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _tokenSent = MutableStateFlow(false)
    val tokenSent = _tokenSent.asStateFlow()

    private val tokenManager: TokenManager by inject()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _verificationCode = MutableStateFlow("")
    val verificationCode = _verificationCode.asStateFlow()

//    private val _isCodeSent = MutableStateFlow(false)
//    val isCodeSent = _isCodeSent.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _tokenFetched = MutableStateFlow(false)
    val tokenFetched: StateFlow<Boolean> = _tokenFetched.asStateFlow()

    private var verificationId: String = ""

    fun updatePhoneNumber(number: String) {
        _phoneNumber.value = number
    }

    fun updateVerificationCode(code: String) {
        _verificationCode.value = code
    }

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    fun signOut() {
        Firebase.auth.signOut()
        _isLoggedIn.value = false
    }

    fun signIn() {
        _isLoggedIn.value = true
    }

    fun sendVerificationCode() {
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // Handle error
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@PhoneAuthViewModel.verificationId = verificationId
//                _isCodeSent.value = true
                _authState.value = AuthState.CodeSent
            }
        }

        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(_phoneNumber.value)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyCode() {
        val credential = PhoneAuthProvider.getCredential(verificationId, _verificationCode.value)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login", "successful login")
                    signIn()
                    _authState.value = AuthState.Success(auth.currentUser)
                } else {
                    // Sign in failed
                    Log.d("Login", "failed login")
                    _authState.value = AuthState.Error(task.exception?.message ?: "Sign in failed")
                }
            }
    }

    fun sendIdTokenToServer(idToken: String) {
        if (!tokenSent.value) {

            val client = OkHttpClient()

            val requestBody = FormBody.Builder()
                .build()

            val request = Request.Builder()
                .url(YOUR_SERVER_URL) // your server URL
                .header("Authorization", "Bearer $idToken")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // TODO: Show an error message to the user
                    // TODO: Retry the request after a delay
                    Log.e("SignIn", "Failed to send ID token", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // Handle successful response
                        Log.d("SignIn", "ID token sent successfully")

                        _tokenSent.value = true
                        // TODO: Navigate to the home screen or perform other actions
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                response.body?.let {
                                    // Use token from server
                                    val responseJson = it.string()
                                    it.close()

                                    try {
                                        val jsonBuilder = Json { ignoreUnknownKeys = true }
                                        val loginResult =
                                            jsonBuilder.decodeFromString<LoginResult>(responseJson)

                                        tokenManager.saveToken(loginResult.token)
                                    } catch (e: Exception) {
                                        Log.e("SignIn", "Error parsing response", e)
                                    }
                                }
                            }
                        }
                    } else {
                        // Handle error response
                        Log.e("SignIn", "Error sending ID token: ${response.code}")
                        viewModelScope.launch {
                            withContext(Dispatchers.Main) {
                                // Show error message to the user
                            }
                        }
                    }
                }
            })
        }
    }

    fun getIntegrityToken() {
        viewModelScope.launch {
            try {

                val result = withContext(Dispatchers.IO) {
                    val nonce: String = generateNonce()
                    val integrityManager = IntegrityManagerFactory.create(applicationContext)

                    val integrityTokenResponse: Task<IntegrityTokenResponse> =
                        integrityManager.requestIntegrityToken(
                            IntegrityTokenRequest.builder()
                                .setCloudProjectNumber(YOUR_PROJECT_NUMBER)
                                .setNonce(nonce)
                                .build()
                        )
                    integrityTokenResponse.addOnSuccessListener { resp ->
                        val integrityToken: String = resp.token()
                        _tokenFetched.value = true
                        // Store the token or use it immediately
                    }
                    integrityTokenResponse.addOnFailureListener { e ->
                        Log.e("IntegrityAPI", "Failed integration token fetch", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("IntegrityAPI", "Error getting integrity token", e)
                // Handle the error, possibly by returning a default value or throwing an exception
            }
        }
    }

    private fun generateNonce(): String {
        val nonce = ByteArray(16) // 16 bytes = 128 bits
        SecureRandom().nextBytes(nonce)
        return Base64.encodeToString(nonce, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}
