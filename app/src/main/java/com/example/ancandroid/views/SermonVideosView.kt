package com.example.ancandroid.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ancandroid.data.LoadingState
import com.example.ancandroid.data.YouTubeVideo
import com.example.ancandroid.openUrlInExternalBrowser
import com.example.ancandroid.viewmodel.VideosViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SermonVideosView() {
    val videosViewModel: VideosViewModel = koinViewModel()
    val videosUIState by videosViewModel.videosUIState.collectAsStateWithLifecycle()

    when (videosUIState.loadingState) {
        LoadingState.Loading -> {
//            Text("Loading")
            VideosLoadingView()
        }

        LoadingState.Success -> {
            VideosGrid(
                videos = videosUIState.videos,
                clickFunc = {
                    openUrlInExternalBrowser("https://youtu.be/${it.videoId}")
                }
            )
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
fun VideosGrid(videos: List<YouTubeVideo>, clickFunc: (YouTubeVideo) -> Unit) {

    LazyColumn(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .fillMaxHeight()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(
            items = videos,
            itemContent = { video: YouTubeVideo ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Row(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.surface)
                            .padding(14.dp)
                            .clickable {
                                // start VideoActivity and pass the video details
                                clickFunc(video)
                            },
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            modifier = Modifier
                                .height(100.dp)
                                .width(180.dp)
                                .clip(MaterialTheme.shapes.small),
                            contentScale = ContentScale.Crop,
                        )
                        Text(
                            text = video.title,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun VideosLoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Loading...")
        //        CircularProgressIndicator()
    }
}
