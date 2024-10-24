package com.anconnuri.ancandroid.viewmodel

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object CodeSent : AuthState()
    data class Success(val user: FirebaseUser?) : AuthState()
}