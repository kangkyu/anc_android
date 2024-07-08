package com.example.ancandroid.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

private const val baseUrl = "https://anc-backend-7502ef948715.herokuapp.com"

class ChurchAPI {

    companion object {
        val shared = ChurchAPI()
    }

    private val client = HttpClient()

    suspend fun getJuboExternalURL(): Result<String> {
        return runCatching {
            val response: HttpResponse = client.get("${baseUrl}/jubo.json")
            // client.close()
            // commented out to avoid JobCancellationException
            response.body()
        }
    }

    suspend fun getVideos(): Result<String> {
        return runCatching {
            val response: HttpResponse = client.get("${baseUrl}/videos")
            // client.close()
            // commented out to avoid JobCancellationException
            response.body()
        }
    }
}

enum class LoadingState {
    Loading,
    Success,
    Failure,
    Error
}
