package com.anconnuri.ancandroid.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.ChurchAPI
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.utils.TokenManager
import com.anconnuri.ancandroid.views.PrayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PrayerViewModel : ViewModel(), KoinComponent {
    private val tokenManager: TokenManager by inject()

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _isTokenValid = MutableStateFlow(true)
    val isTokenValid = _isTokenValid.asStateFlow()

    var isRefreshing by mutableStateOf(false)

    // TODO: this is temporary
    init {
        getPrayer()
    }

    // TODO: decide and add a 'next' button on PrayerView
    fun refresh() {
        isRefreshing = true
        try {
            getPrayer()
        } finally {
            isRefreshing = false
        }
    }

    fun getPrayer() {
        _uiState.value = PrayerUiState(loadingState = LoadingState.Loading)
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    _isTokenValid.value = false
                    _uiState.update {
                        it.copy(loadingState = LoadingState.Error, error = "No token available")
                    }
                    return@launch
                }

                ChurchAPI.shared.getFirstPrayer(token).onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            loadingState = LoadingState.Success,
                            prayer = result
                        )
                    }
                }.onFailure { result ->
                    _uiState.update {
                        it.copy(loadingState = LoadingState.Failure, error = result.toString())
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingState = LoadingState.Error, error = e.message)
                }
            }
        }
    }
}