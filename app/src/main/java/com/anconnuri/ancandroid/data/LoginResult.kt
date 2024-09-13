package com.anconnuri.ancandroid.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResult(
    @SerialName("token")
    val token: String
)
