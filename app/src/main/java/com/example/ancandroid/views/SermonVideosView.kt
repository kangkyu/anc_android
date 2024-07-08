package com.example.ancandroid.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import coil.compose.AsyncImage
import com.example.ancandroid.data.LoadingState
import com.example.ancandroid.data.ChurchAPI
import com.example.ancandroid.data.Video
import com.example.ancandroid.openUrlInExternalBrowser
import kotlinx.coroutines.IO

@Composable
fun SermonVideosView() {
    val videos = remember { mutableStateOf<List<YouTubeVideo>>(listOf()) }
    var loadingState: LoadingState by remember { mutableStateOf(LoadingState.Loading) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                ChurchAPI.shared.getVideos().onSuccess {
                    videos.value = Json.decodeFromString<List<Video>>(it).toYouTubeVideos()
                    loadingState = LoadingState.Success
                }.onFailure {
                    loadingState = LoadingState.Failure
                }
            } catch (e: Exception) {
                println(e)
                loadingState = LoadingState.Error
            }
        }
    }

    when (loadingState) {
        LoadingState.Loading -> {
//            Text("Loading")
            VideosLoadingView()
        }

        LoadingState.Success -> {
            VideosGrid(videos = videos.value, clickFunc = { openUrlInExternalBrowser("https://youtu.be/${it.videoId}") })
        }

        LoadingState.Failure -> {
            Text("Failure")
        }

        LoadingState.Error -> {
            Text("Error")
        }
    }
}

fun List<Video>.toYouTubeVideos(): List<YouTubeVideo> {
    return this.map {
        YouTubeVideo(it.id, it.title, it.thumbnailUrl)
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

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String
)

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
