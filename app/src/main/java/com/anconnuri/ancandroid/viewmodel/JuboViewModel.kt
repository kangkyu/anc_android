package com.anconnuri.ancandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.ChurchAPI
import com.anconnuri.ancandroid.data.ExternalURL
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.views.JuboUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlin.coroutines.cancellation.CancellationException

class JuboViewModel(): ViewModel() {
    // Change MutableStateFlow to StateFlow for public access
    private val _juboUIState = MutableStateFlow(JuboUIState())
    val juboUIState = _juboUIState.asStateFlow()

    init {
        getJubo()
    }


    private fun getJubo() {
        _juboUIState.update { it.copy(loadingState = LoadingState.Loading) }

        viewModelScope.launch(Dispatchers.IO) {  // IO dispatcher for network calls
            ChurchAPI.shared.getJuboExternalURL()
                .fold(
                    onSuccess = { result ->
                        try {
                            val externalUrl = Json.decodeFromString<ExternalURL>(result)
                            _juboUIState.update {
                                it.copy(
                                    loadingState = LoadingState.Success,
                                    externalURL = externalUrl
                                )
                            }
                        } catch (e: Exception) {
                            _juboUIState.update {
                                it.copy(
                                    loadingState = LoadingState.Error,
                                    error = "Failed to parse data: ${e.message}"
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _juboUIState.update {
                            it.copy(
                                loadingState = LoadingState.Failure,
                                error = error.message
                            )
                        }
                    }
                )
        }
    }

    fun retryLoading() {
        getJubo()
    }
}
