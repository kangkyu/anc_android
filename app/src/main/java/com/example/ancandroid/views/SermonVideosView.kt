package com.example.ancandroid.views

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
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
        modifier = Modifier.fillMaxHeight()
    ) {
        items(
            items = videos,
            itemContent = { video: YouTubeVideo ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                // start VideoActivity and pass the video details
                                clickFunc(video)
                            },
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            modifier = Modifier.height(99.dp).width(176.dp),
                            contentScale = ContentScale.Crop,
                        )
                        Text(
                            text = video.title,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().height(1.dp),
                        color = Color.Black
                    )
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
