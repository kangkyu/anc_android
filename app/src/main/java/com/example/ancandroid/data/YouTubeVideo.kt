package com.example.ancandroid.data

data class YouTubeVideo(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String
)

fun List<Video>.toYouTubeVideos(): List<YouTubeVideo> {
    return this.map {
        YouTubeVideo(it.id, it.title, it.thumbnailUrl)
    }
}
