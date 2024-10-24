package com.anconnuri.ancandroid.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val baseUrl = "https://anc-backend-7502ef948715.herokuapp.com"

class ChurchAPI {

    companion object {
        val shared = ChurchAPI()
    }

    // Make client lazy to ensure single instance
    private val client by lazy {
        HttpClient {
            // Add any client configurations here
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
            }
        }
    }

    private val jsonBuilder = Json { ignoreUnknownKeys = true }

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

    suspend fun addPrayerRequest(tokenString: String, content: String): Result<Prayer> {
        return runCatching {
            val response: HttpResponse = client.post("${baseUrl}/prayers") {
                applyDefaultHeaders(tokenString)
                setBody(jsonBuilder.encodeToString(PrayerRequest(content = content)))
            }

            when (response.status) {
                HttpStatusCode.Created -> {
                    val createdPrayerJson = response.bodyAsText()
                    val createdPrayer = jsonBuilder.decodeFromString<Prayer>(createdPrayerJson)
                    createdPrayer
                }
                else -> {
                    throw Exception("Unexpected response: ${response.status.value}")
                }
            }
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

    suspend fun prayPrayer(tokenString: String, id: Int): Result<Prayer?> {
        return runCatching {
            val response: HttpResponse = client.post("${baseUrl}/prayers/$id/pray") {
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
}

enum class LoadingState {
    Loading,
    Success,
    Failure,
    Error
}
