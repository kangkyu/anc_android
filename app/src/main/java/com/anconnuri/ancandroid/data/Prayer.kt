package com.anconnuri.ancandroid.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PrayerRequest(
    @SerialName("content")
    val content: String
)

@Serializable
data class Prayer(
    @SerialName("content")
    val content: String,
    @SerialName("id")
    val id: Int,
    @SerialName("created_at")
    val createdAt: String? = null
    // Add other fields that the server returns
)
