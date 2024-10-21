package com.anconnuri.ancandroid.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Prayer
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import org.koin.androidx.compose.koinViewModel


@Composable
fun PrayerScreen(onAddPrayer: () -> Unit, onSignOut: () -> Unit) {
    val viewModel: PrayerViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()

    var currentPage by remember { mutableIntStateOf(1) }

    LaunchedEffect(currentPage) {
        if (hasMorePages) {
            viewModel.getPrayer(currentPage)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            when (loadingState) {
                LoadingState.Loading -> CircularProgressIndicator()
                LoadingState.Success -> {
                    uiState.prayer?.let { prayer ->
                        PrayerContent(
                            prayer = prayer,
                            onPray = {
                                viewModel.prayPrayer(prayer.id)
                            }
                        )
                    }
                }
                LoadingState.Error, LoadingState.Failure -> {
                    Text(uiState.error ?: "An error occurred", color = MaterialTheme.colorScheme.error)
                }
            }
        }

        Row {
            Button(
                onClick = {
                    currentPage -= 1
                    viewModel.setHasMorePages(true)
                },
                enabled = currentPage > 1
            ) {
                Text("Backward")
            }

            Spacer(modifier = Modifier.weight(1f)) // Push buttons to the sides

            Button(
                onClick = {
                    currentPage += 1
                },
                enabled = hasMorePages
            ) {
                Text("Forward")
            }
        }

        Button(
            onClick = onAddPrayer,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Prayer")
        }

        Button(
            onClick = onSignOut,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Log Out")
        }
    }
}

@Composable
fun PrayerContent(prayer: Prayer, onPray: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = prayer.content,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        Text("${prayer.counter} praying")
        Button(
            onClick = onPray,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("I pray")
        }
    }
}
