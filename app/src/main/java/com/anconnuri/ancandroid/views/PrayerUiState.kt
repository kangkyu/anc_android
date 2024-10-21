package com.anconnuri.ancandroid.views

import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Prayer

data class PrayerUiState(
    val prayer: Prayer? = null,
    val error: String? = null
)
