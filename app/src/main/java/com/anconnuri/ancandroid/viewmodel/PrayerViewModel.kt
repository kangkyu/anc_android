package com.anconnuri.ancandroid.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anconnuri.ancandroid.data.ChurchAPI
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.utils.TokenManager
import com.anconnuri.ancandroid.views.PrayerUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.withContext

class PrayerViewModel : ViewModel(), KoinComponent {
    private val tokenManager: TokenManager by inject()

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState = _uiState.asStateFlow()

    private val _loadingState = MutableStateFlow(LoadingState.Success)
    val loadingState = _loadingState.asStateFlow()

    private val _formState = MutableStateFlow<FormState>(FormState.Idle)
    val formState = _formState.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    fun getPrayer(page: Int) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val token = tokenManager.getToken()
                if (token == null) {
                    handleInvalidToken()
                    return@launch
                }

                val result = withContext(Dispatchers.IO) {
                    ChurchAPI.shared.getPagedPrayer(token, page)
                }

                result.onSuccess { prayerResult ->
                    _uiState.update { it.copy(prayer = prayerResult, error = null) }
                    _hasMorePages.value = prayerResult != null
                    _loadingState.value = LoadingState.Success
                }.onFailure { error ->
                    _uiState.update { it.copy(error = error.toString()) }
                    _loadingState.value = LoadingState.Failure
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
                _loadingState.value = LoadingState.Error
            }
        }
    }

    fun setHasMorePages(value: Boolean) {
        _hasMorePages.value = value
    }

    private fun handleInvalidToken() {
        _uiState.update { it.copy(error = "Invalid token") }
        _loadingState.value = LoadingState.Error
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

    fun prayPrayer(id: Int) {
        viewModelScope.launch {
            val token = tokenManager.getToken()
            if (token == null) {
                handleInvalidToken()
                return@launch
            }
            ChurchAPI.shared.prayPrayer(token, id).onSuccess { result ->
                _uiState.update {
                    it.copy(prayer = result)
                }
            }.onFailure { result ->
                _uiState.update {
                    it.copy(error = result.toString())
                }
                _formState.value = FormState.Error("An error occurred")
            }
        }
    }
}

sealed class FormState {
    data object Idle : FormState()
    data object Submitting : FormState()
    data object Submitted : FormState()
    data class Error(val message: String) : FormState()
}
