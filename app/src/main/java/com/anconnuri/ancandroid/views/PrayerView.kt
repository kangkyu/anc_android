package com.anconnuri.ancandroid.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.anconnuri.ancandroid.ui.theme.customBlue
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

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        currentPage -= 1
                        viewModel.setHasMorePages(true)
                    },
                    enabled = currentPage > 1
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("<", modifier = Modifier.padding(end = 4.dp))
                        Text("이전")
                    }
                }

                Text(
                    "기도제목",
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                TextButton(
                    onClick = { currentPage += 1 },
                    enabled = hasMorePages
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("다음")
                        Text(">", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            // Scrollable Content Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(bottom = 140.dp),
                contentAlignment = Alignment.Center
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
                        Text(
                            uiState.error ?: "An error occurred",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        // Bottom Buttons
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onAddPrayer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("내 기도제목 올리기")
            }

            Button(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("로그아웃")
            }
        }
    }
}

@Composable
fun PrayerContent(prayer: Prayer, onPray: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Prayer content at the top
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = prayer.content,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Column() {

                Text(
                    "${prayer.counter} 명이 기도했습니다",
                    modifier = Modifier.fillMaxWidth().padding(),
                    textAlign = TextAlign.End
                )
                // Pray button at the bottom
                Button(
                    onClick = onPray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    enabled = !prayer.userPrayed
                ) {
                    Text("기도합니다")
                }
            }
        }
    }
}
