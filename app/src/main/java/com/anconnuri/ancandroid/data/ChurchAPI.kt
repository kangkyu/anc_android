package com.anconnuri.ancandroid.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json

private const val baseUrl = "https://anc-backend-7502ef948715.herokuapp.com"

class ChurchAPI {

    companion object {
        val shared = ChurchAPI()
    }

    private val client = HttpClient()

    suspend fun getJuboExternalURL(): Result<String> {
        return runCatching {
            val response: HttpResponse = client.get("${baseUrl}/jubo.json")
            response.body()
        }
    }

    suspend fun getVideos(): Result<String> {
        return runCatching {
            val response: HttpResponse = client.get("${baseUrl}/videos")
            response.body()
        }
    }

    suspend fun getFirstPrayer(tokenString: String): Result<Prayer> {
        return runCatching {
            val response: HttpResponse = client.get("${baseUrl}/prayers/1") {
                applyDefaultHeaders(tokenString)
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    val prayerJson = response.bodyAsText()
                    val jsonBuilder = Json { ignoreUnknownKeys = true }
                    val prayer = jsonBuilder.decodeFromString<Prayer>(prayerJson)
                    prayer
                }
                else -> {
                    throw Exception("Unexpected response: ${response.status.value}")
                }
            }
        }
    }

    private fun HttpRequestBuilder.applyDefaultHeaders(tokenString: String) {
        headers {
            append(HttpHeaders.Accept, "application/json")
            append(HttpHeaders.ContentType, "application/json")
            append(HttpHeaders.UserAgent, "Android Ktor")
            append(HttpHeaders.Authorization, "Bearer $tokenString")
        }
    }

    suspend fun getPagedPrayer(tokenString: String, page: Int): Result<Prayer?> {
            return runCatching {
                val response: HttpResponse = client.get("${baseUrl}/prayers?page=$page") {
                    applyDefaultHeaders(tokenString)
                }

                when (response.status) {
                    HttpStatusCode.OK -> {
                        val prayerJson = response.bodyAsText()
                        val jsonBuilder = Json { ignoreUnknownKeys = true }
                        val prayer = jsonBuilder.decodeFromString<List<Prayer>>(prayerJson).firstOrNull()
                        prayer
                    }
                    else -> {
                        throw Exception("Unexpected response: ${response.status.value}")
                    }
                }
            }
    }

    fun close() {
        client.close()
    }
}

enum class LoadingState {
    Loading,
    Success,
    Failure,
    Error
}
