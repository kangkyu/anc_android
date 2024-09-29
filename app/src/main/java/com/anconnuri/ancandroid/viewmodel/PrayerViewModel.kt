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
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job

class PrayerViewModel : ViewModel(), KoinComponent {
    private val tokenManager: TokenManager by inject()

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _isTokenValid = MutableStateFlow(true)
    val isTokenValid = _isTokenValid.asStateFlow()

    private var fetchJob: Job? = null

    init {
        getPrayer(0) // Fetch initial prayer when ViewModel is created
    }

    fun getPrayer(page: Int) {
        fetchJob?.cancel() // Cancel any ongoing fetch job
        fetchJob = viewModelScope.launch {
            _uiState.update { it.copy(loadingState = LoadingState.Loading) }
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    handleInvalidToken()
                    return@launch
                }

                ChurchAPI.shared.getPagedPrayer(token, page+1).onSuccess { result ->
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

    fun onAnotherPrayerRequested(page: Int) {
        viewModelScope.launch {
            delay(300) // Add a small delay to prevent rapid-fire API calls
            getPrayer(page+1)
        }
    }

    fun onAnotherPrayerRequestedBackward(page: Int) {
        viewModelScope.launch {
            delay(300) // Add a small delay to prevent rapid-fire API calls
            getPrayer(page)
        }
    }

    private fun handleInvalidToken() {
        _isTokenValid.value = false
        _uiState.update {
            it.copy(loadingState = LoadingState.Error, error = "No token available")
        }
    }
}
