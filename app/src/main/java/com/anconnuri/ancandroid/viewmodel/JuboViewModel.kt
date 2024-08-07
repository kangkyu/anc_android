package com.anconnuri.ancandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.ChurchAPI
import com.anconnuri.ancandroid.data.ExternalURL
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.views.JuboUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class JuboViewModel(): ViewModel() {
    val juboUIState = MutableStateFlow(JuboUIState())

    init {
        getJubo()
    }

    private fun getJubo() {
        juboUIState.value = JuboUIState(loadingState = LoadingState.Loading)
        viewModelScope.launch {
            try {
                ChurchAPI.shared.getJuboExternalURL().onSuccess { result ->
                    juboUIState.update {
                        it.copy(
                            loadingState = LoadingState.Success,
                            externalURL = Json.decodeFromString<ExternalURL>(result)
                        )
                    }
                }.onFailure { result ->
                    juboUIState.update {
                        it.copy(loadingState = LoadingState.Failure, error = result.toString())
                    }
                }
            } catch (e: Exception) {
                juboUIState.update {
                    it.copy(loadingState = LoadingState.Error, error = e.message)
                }
            }
        }
    }
}
