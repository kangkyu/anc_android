package com.anconnuri.ancandroid.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Prayer
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrayerScreen(onAddPrayer: () -> Unit) {
    val viewModel: PrayerViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val hasMorePages by viewModel.hasMorePages.collectAsState()
    //    val coroutineScope = rememberCoroutineScope()

    var currentPage by remember { mutableStateOf(1) }

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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.loadingState) {
                LoadingState.Loading -> CircularProgressIndicator()
                LoadingState.Success -> {
                    uiState.prayer?.let { prayer ->
                        PrayerContent(
                            prayer = prayer,
                            onPray = {}
                        )
                    }
                }
                LoadingState.Error, LoadingState.Failure -> {
                    Text(uiState.error ?: "An error occurred", color = MaterialTheme.colorScheme.error)
                }
                else -> Text("No prayer available")
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
        }

        Button(
            onClick = onAddPrayer,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Add Prayer")
        }
    }
}

@Composable
fun PrayerContent(prayer: Prayer, onPray: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
        Button(
            onClick = onPray,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("I pray")
        }
    }
}
