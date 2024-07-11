package com.anconnuri.ancandroid.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("thumbnail_url")
    val thumbnailUrl: String
)
