package com.anconnuri.ancandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.ChurchAPI
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Video
import com.anconnuri.ancandroid.data.toYouTubeVideos
import com.anconnuri.ancandroid.views.VideosUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class VideosViewModel(): ViewModel() {
    val videosUIState = MutableStateFlow(VideosUIState())

    init {
        getVideos()
    }

    private fun getVideos() {
        videosUIState.value = VideosUIState(loadingState = LoadingState.Loading)
        viewModelScope.launch {
            try {
                ChurchAPI.shared.getVideos().onSuccess { result ->
                    videosUIState.update {
                        it.copy(loadingState = LoadingState.Success, videos = Json.decodeFromString<List<Video>>(result).toYouTubeVideos())
                    }
                }.onFailure { result ->
                    videosUIState.update {
                        it.copy(loadingState = LoadingState.Failure, error = result.toString())
                    }
                }
            } catch (e: Exception) {
                videosUIState.update {
                    it.copy(loadingState = LoadingState.Error, error = e.message)
                }
            }
        }
    }
}
