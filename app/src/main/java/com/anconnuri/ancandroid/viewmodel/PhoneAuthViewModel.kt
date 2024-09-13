package com.anconnuri.ancandroid.viewmodel


import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit
import android.util.Log
import com.anconnuri.ancandroid.views.AuthState
import com.google.firebase.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PhoneAuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _verificationCode = MutableStateFlow("")
    val verificationCode = _verificationCode.asStateFlow()

//    private val _isCodeSent = MutableStateFlow(false)
//    val isCodeSent = _isCodeSent.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

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
}