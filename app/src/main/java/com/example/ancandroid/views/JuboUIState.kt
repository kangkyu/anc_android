package com.example.ancandroid.views

import com.example.ancandroid.data.ExternalURL
import com.example.ancandroid.data.LoadingState

data class JuboUIState(
    val loadingState: LoadingState = LoadingState.Loading,
    val error: String? = null,
    val externalURL: ExternalURL? = null
)
