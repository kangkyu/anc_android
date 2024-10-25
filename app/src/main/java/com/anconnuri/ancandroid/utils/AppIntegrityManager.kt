package com.anconnuri.ancandroid.utils

import android.app.Application
import android.content.Context
import android.util.Base64
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.SecureRandom

const val YOUR_PROJECT_NUMBER = 684076341065

class AppIntegrityManager(
    private val context: Application
) {
    private val _integrityCheckPassed = MutableStateFlow(false)
    val integrityCheckPassed = _integrityCheckPassed.asStateFlow()

    private val _isVerifyingIntegrity = MutableStateFlow(false)
    val isVerifyingIntegrity = _isVerifyingIntegrity.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun verifyIntegrity(scope: CoroutineScope, onComplete: (Boolean) -> Unit) {
        if (integrityCheckPassed.value) {
            onComplete(true)
            return
        }
        if (_isVerifyingIntegrity.value) return
        _isVerifyingIntegrity.value = true

        try {
            IntegrityManagerFactory.create(context)
                .requestIntegrityToken(
                    IntegrityTokenRequest.builder()
                        .setCloudProjectNumber(YOUR_PROJECT_NUMBER)
                        .setNonce(generateNonce())
                        .build()
                ).addOnCompleteListener { task ->
                    scope.launch {
                        if (task.isSuccessful) {
                            _integrityCheckPassed.value = true
                            _errorMessage.value = null

                            onComplete(true)
                        } else {
                            _integrityCheckPassed.value = false
                            _errorMessage.value = "Device verification failed. Please try again."
                            onComplete(false)
                        }
                        _isVerifyingIntegrity.value = false
                    }
                }
        } catch (e: Exception) {
            Log.e("IntegrityAPI", "Error verifying integrity", e)
            scope.launch {
                _errorMessage.value = "Device verification failed. Please try again."
                _isVerifyingIntegrity.value = false
                onComplete(false)
            }
        }
    }

    private fun generateNonce(): String {
        val nonce = ByteArray(16)
        SecureRandom().nextBytes(nonce)
        return Base64.encodeToString(nonce, Base64.URL_SAFE or Base64.NO_WRAP)
    }
}
