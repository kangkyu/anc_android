package com.example.ancandroid.views

import com.example.ancandroid.data.LoadingState
import com.example.ancandroid.data.YouTubeVideo

data class VideosUIState(
    val loadingState: LoadingState = LoadingState.Loading,
    val videos: List<YouTubeVideo> = emptyList(),
    val error: String? = null
)
