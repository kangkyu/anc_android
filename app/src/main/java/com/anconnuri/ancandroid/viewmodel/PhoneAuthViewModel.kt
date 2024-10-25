package com.anconnuri.ancandroid.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.CountryCode
import com.anconnuri.ancandroid.data.LoginResult
import com.anconnuri.ancandroid.data.countryCodes
import com.anconnuri.ancandroid.utils.AppIntegrityManager
import com.anconnuri.ancandroid.utils.TokenManager
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

const val YOUR_SERVER_URL = "https://anc-backend-7502ef948715.herokuapp.com/auth/firebase-auth"
const val YOUR_PROJECT_NUMBER = 684076341065

class PhoneAuthViewModel : ViewModel(), KoinComponent {
    private val applicationContext by inject<Context>()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val tokenManager: TokenManager by inject()
    private val appIntegrityManager: AppIntegrityManager by inject()

    // User input states
    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _verificationCode = MutableStateFlow("")
    val verificationCode = _verificationCode.asStateFlow()

    private val _selectedCountryCode = MutableStateFlow(countryCodes.first())
    val selectedCountryCode: StateFlow<CountryCode> = _selectedCountryCode

    // Error states
    private val _phoneNumberError = MutableStateFlow<String?>(null)
    val phoneNumberError: StateFlow<String?> = _phoneNumberError

    private val _verificationCodeError = MutableStateFlow<String?>(null)
    val verificationCodeError = _verificationCodeError.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // Auth states
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _tokenSent = MutableStateFlow(false)
    val tokenSent = _tokenSent.asStateFlow()

    val integrityCheckPassed = appIntegrityManager.integrityCheckPassed
    val isVerifyingIntegrity = appIntegrityManager.isVerifyingIntegrity
    val integrityErrorMessage = appIntegrityManager.errorMessage

    private var verificationId: String = ""

    private fun resetStates() {
        _authState.value = AuthState.Idle
        _isLoggedIn.value = false
        _tokenSent.value = false
        _phoneNumber.value = ""
        _verificationCode.value = ""
        _phoneNumberError.value = null
        _verificationCodeError.value = null
        _errorMessage.value = null
    }

    fun signOut() {
        Firebase.auth.signOut()
        resetStates()
    }

    // Input handling
    fun updatePhoneNumber(number: String) {
        _phoneNumber.value = number
        _phoneNumberError.value = null
    }

    fun updateVerificationCode(code: String) {
        _verificationCode.value = code
        _verificationCodeError.value = null
    }

    fun updateCountryCode(countryCode: CountryCode) {
        _selectedCountryCode.value = countryCode
    }

    // Validation
    private fun validatePhoneNumber(): Boolean {
        return when {
            _phoneNumber.value.isEmpty() -> {
                _phoneNumberError.value = "Phone number cannot be empty"
                false
            }
            _phoneNumber.value.length != 10 -> {
                _phoneNumberError.value = "Phone number must be 10 digits"
                false
            }
            else -> {
                _phoneNumberError.value = null
                true
            }
        }
    }

    private fun validateVerificationCode(): Boolean {
        return when {
            _verificationCode.value.isEmpty() -> {
                _verificationCodeError.value = "Verification code cannot be empty"
                false
            }
            else -> {
                _verificationCodeError.value = null
                true
            }
        }
    }

    fun sendVerificationCode() {
        if (!validatePhoneNumber()) return

        if (!integrityCheckPassed.value) {
            _authState.value = AuthState.Loading
            appIntegrityManager.verifyIntegrity(viewModelScope) { passed ->
                if (passed) {
                    proceedWithVerification()
                } else {
                    _authState.value = AuthState.Idle
                }
            }
            return
        }
        proceedWithVerification()
    }

    private fun proceedWithVerification() {
        _authState.value = AuthState.Loading
        val fullPhoneNumber = "${selectedCountryCode.value.prefix}${phoneNumber.value}"

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _authState.value = AuthState.Idle
                _phoneNumberError.value = e.message
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                this@PhoneAuthViewModel.verificationId = verificationId
                _authState.value = AuthState.CodeSent
            }
        }

        PhoneAuthProvider.verifyPhoneNumber(
            PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(fullPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build()
        )
    }

    fun verifyCode() {
        if (!validateVerificationCode()) return

        _authState.value = AuthState.Loading
        val credential = PhoneAuthProvider.getCredential(verificationId, _verificationCode.value)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _isLoggedIn.value = true
                    _authState.value = AuthState.Success(auth.currentUser)
                    auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        sendIdTokenToServer(result.token ?: "")
                    }
                } else {
                    _verificationCodeError.value = task.exception?.message ?: "Sign in failed"
                    _authState.value = AuthState.CodeSent
                }
            }
    }

    fun sendIdTokenToServer(idToken: String) {
        if (tokenSent.value) return

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(YOUR_SERVER_URL)
            .header("Authorization", "Bearer $idToken")
            .post(FormBody.Builder().build())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                viewModelScope.launch {
                    _errorMessage.value = "Failed to communicate with server"
                    _tokenSent.value = false
                }
            }

            override fun onResponse(call: Call, response: Response) {
                viewModelScope.launch {
                    response.body?.let {
                        try {
                            val responseJson = it.string()
                            it.close()
                            val loginResult = Json { ignoreUnknownKeys = true }
                                .decodeFromString<LoginResult>(responseJson)
                            tokenManager.saveToken(loginResult.token)
                            _tokenSent.value = true
                        } catch (e: Exception) {
                            _errorMessage.value = "Server response error"
                            _tokenSent.value = false
                        }
                    }
                }
            }
        })
    }
}
