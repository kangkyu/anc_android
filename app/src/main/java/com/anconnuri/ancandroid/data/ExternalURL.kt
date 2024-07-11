package com.anconnuri.ancandroid.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalURL(
    @SerialName("external_url")
    val url: String,
    @SerialName("image_urls")
    val urls: List<String>
)
