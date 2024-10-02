package com.anconnuri.ancandroid.viewmodel

import androidx.compose.runtime.mutableStateOf
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

    private val _formState = MutableStateFlow<FormState>(FormState.Idle)
    val formState = _formState.asStateFlow()

    private val _hasMorePages = MutableStateFlow<Boolean>(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    private var fetchJob: Job? = null

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

                ChurchAPI.shared.getPagedPrayer(token, page).onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            loadingState = LoadingState.Success,
                            prayer = result
                        )
                    }
                    _hasMorePages.value = result != null
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(loadingState = LoadingState.Failure, error = error.toString())
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loadingState = LoadingState.Error, error = e.message)
                }
            }
        }
    }

    fun setHasMorePages(value: Boolean) {
        _hasMorePages.value = value
    }

    private fun handleInvalidToken() {
        _uiState.update {
            it.copy(loadingState = LoadingState.Error, error = "No token available")
        }
    }

    fun submitPrayerRequest(content: String) {

        viewModelScope.launch {

            val token = tokenManager.getToken()
            if (token == null) {
                handleInvalidToken()
                return@launch
            }

            _formState.value = FormState.Submitting
            ChurchAPI.shared.addPrayerRequest(token, content).onSuccess { result ->
                _uiState.update {
                    it.copy(prayer = result)
                }
                _formState.value = FormState.Submitted
            }.onFailure { result ->
                _uiState.update {
                    it.copy(error = result.toString())
                }
                _formState.value = FormState.Error("An error occurred")
            }
        }
    }

    fun resetFormState() {
        _formState.value = FormState.Idle
    }
}

sealed class FormState {
    object Idle : FormState()
    object Submitting : FormState()
    object Submitted : FormState()
    data class Error(val message: String) : FormState()
}
