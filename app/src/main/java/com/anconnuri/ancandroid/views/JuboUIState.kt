package com.anconnuri.ancandroid.views

import com.anconnuri.ancandroid.data.ExternalURL
import com.anconnuri.ancandroid.data.LoadingState

data class JuboUIState(
    val loadingState: LoadingState = LoadingState.Loading,
    val error: String? = null,
    val externalURL: ExternalURL? = null
)
