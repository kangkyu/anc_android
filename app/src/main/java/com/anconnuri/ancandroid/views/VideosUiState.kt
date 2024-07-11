package com.anconnuri.ancandroid.views

import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.YouTubeVideo

data class VideosUIState(
    val loadingState: LoadingState = LoadingState.Loading,
    val videos: List<YouTubeVideo> = emptyList(),
    val error: String? = null
)
