package com.anconnuri.ancandroid.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.anconnuri.ancandroid.data.ExternalURL
import com.anconnuri.ancandroid.data.LoadingState
import com.anconnuri.ancandroid.viewmodel.JuboViewModel
import org.koin.androidx.compose.koinViewModel
import coil.request.ImageRequest

@Composable
fun JuboView() {
    val juboViewModel: JuboViewModel = koinViewModel()
    val juboUIState by juboViewModel.juboUIState.collectAsStateWithLifecycle()

    // Remember the last successful state
    val lastSuccessfulState = remember(juboUIState.externalURL) {
        juboUIState.externalURL
    }

    when (juboUIState.loadingState) {
        LoadingState.Loading -> {
            JuboLoadingView()
        }

        LoadingState.Success -> {
            juboUIState.externalURL?.let { JuboImageView(it) }
        }

        LoadingState.Failure -> {
            ErrorView(
                message = juboUIState.error ?: "Failed to load",
                onRetry = { juboViewModel.retryLoading() }
            )
        }

        LoadingState.Error -> {
            ErrorView(
                message = juboUIState.error ?: "An error occurred",
                onRetry = { juboViewModel.retryLoading() }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JuboImageView(externalLink: ExternalURL) {
    val pagerState = rememberPagerState(pageCount = {
        2
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        VerticalPager(state = pagerState) { page ->
            // Move zoom/pan state inside the pager item
            var scale by remember { mutableStateOf(1f) }
            var offset by remember { mutableStateOf(Offset.Zero) }

            val minScale = 1f
            val maxScale = 3f

            val state = rememberTransformableState { zoomChange, panChange, _ ->
                scale = (scale * zoomChange).coerceIn(minScale, maxScale)
                if (scale > 1f) {
                    offset += panChange
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(externalLink.urls[page])
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                        .transformable(state = state)
                )
            }
        }
    }
}

@Composable
fun JuboLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...")
//        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}
