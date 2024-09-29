package com.anconnuri.ancandroid.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.data.Prayer
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PrayerScreen(viewModel: PrayerViewModel) {
    val pagerState = rememberPagerState(pageCount = { Int.MAX_VALUE })
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Local state to hold the current prayer and error message
    var currentPrayer by remember { mutableStateOf<Prayer?>(null) }
    var currentError by remember { mutableStateOf<String?>(null) }

    // Update local states based on uiState changes
    LaunchedEffect(uiState) {
        when (uiState.loadingState) {
            LoadingState.Loading -> {
                currentPrayer = null
                currentError = null
            }
            LoadingState.Success -> {
                currentPrayer = uiState.prayer
                currentError = null
            }
            LoadingState.Failure, LoadingState.Error -> {
                currentPrayer = null
                currentError = uiState.error
            }
        }
    }

    // Fetch a new prayer when the page changes
    LaunchedEffect(pagerState.currentPage) {
        viewModel.getPrayer(pagerState.currentPage)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.loadingState == LoadingState.Loading) {
                    CircularProgressIndicator()
                } else if (currentPrayer != null) {
                    PrayerItem(prayer = currentPrayer!!)
                } else if (currentError != null) {
                    Text(currentError ?: "An error occurred")
                } else {
                    Text("No prayer available")
                }
            }
        }

        Row {
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        viewModel.onAnotherPrayerRequestedBackward(pagerState.currentPage)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Backward")
            }
            Button(
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        viewModel.onAnotherPrayerRequested(pagerState.currentPage)
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Another")
            }
        }
    }
}

@Composable
fun PrayerView() {
    val viewModel: PrayerViewModel = koinViewModel()
    PrayerScreen(viewModel)
}

@Composable
fun PrayerItem(prayer: Prayer) {
    Text(
        text = prayer.content,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}
