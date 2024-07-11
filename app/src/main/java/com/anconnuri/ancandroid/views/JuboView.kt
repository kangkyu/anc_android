package com.anconnuri.ancandroid.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
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
    val loadState = juboUIState.loadingState

    when (loadState) {
        LoadingState.Loading -> {
            JuboLoadingView()
        }

        LoadingState.Success -> {
            val externalLink = juboUIState.externalURL
            externalLink?.let { JuboImageView(it) }
        }

        LoadingState.Failure -> {
            Text("Failure")
        }

        LoadingState.Error -> {
            Text("Error")
        }
    }
}

@Composable
fun JuboImageView(externalLink: ExternalURL) {
    // set up all transformation states
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale *= zoomChange
        offset += offsetChange
    }
    JuboImage(
        modifier = Modifier
            // apply other transformations like rotation and zoom
            // on the pizza slice emoji
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = scale * offset.x,
                translationY = scale * offset.y
            )
            // add transformable to listen to multitouch transformation events
            // after offset
            .transformable(state = state)
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        externalLink = externalLink
    )
}

@Composable
fun JuboImage(modifier: Modifier = Modifier, externalLink: ExternalURL) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        externalLink.urls.forEach { url ->
            AsyncImage(
                ImageRequest.Builder(LocalContext.current)
                    .data(url)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
            )
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
