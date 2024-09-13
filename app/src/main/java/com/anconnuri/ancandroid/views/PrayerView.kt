package com.anconnuri.ancandroid.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Prayer
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun PrayerView() {
    val viewModel: PrayerViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState.loadingState) {
        LoadingState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        LoadingState.Success -> {
            PrayerItem(prayer = uiState.prayer)
        }

        LoadingState.Failure -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("PrayerView Failure: ${uiState.error}")
            }
        }

        LoadingState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("PrayerView Error: ${uiState.error}")
            }
        }
    }
}

@Composable
fun PrayerItem(prayer: Prayer) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = prayer.content,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}
