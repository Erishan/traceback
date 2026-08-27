package com.erishan.traceback.ai.data

import com.erishan.traceback.ai.domain.BriefException
import com.erishan.traceback.ai.domain.OpenAiClient
import com.erishan.traceback.ai.domain.assistantMessageContent
import com.erishan.traceback.core.net.createPlatformHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class KtorOpenAiClient(
    private val httpClient: HttpClient = createPlatformHttpClient(),
) : OpenAiClient {

    override suspend fun completeChat(
        apiKey: String,
        model: String,
        systemPrompt: String,
        userMessage: String,
    ): String {
        val response: HttpResponse = try {
            httpClient.post(CHAT_COMPLETIONS_URL) {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(
                    ChatCompletionsRequest(
                        model = model,
                        messages = listOf(
                            ChatMessage(role = "system", content = systemPrompt),
                            ChatMessage(role = "user", content = userMessage),
                        ),
                        responseFormat = ResponseFormat(type = "json_object"),
                    )
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            throw BriefException(BriefException.Kind.Network)
        }
        when (response.status.value) {
            200 -> Unit
            401 -> throw BriefException(BriefException.Kind.Unauthorized)
            429 -> throw BriefException(BriefException.Kind.RateLimited)
            else -> throw BriefException(BriefException.Kind.Network)
        }
        val body = try {
            response.bodyAsText()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            throw BriefException(BriefException.Kind.InvalidResponse)
        }
        return assistantMessageContent(body)
            ?: throw BriefException(BriefException.Kind.InvalidResponse)
    }

    companion object {
        const val CHAT_COMPLETIONS_URL = "https://api.openai.com/v1/chat/completions"
    }
}

@Serializable
private data class ChatCompletionsRequest(
    val model: String,
    val messages: List<ChatMessage>,
    @SerialName("response_format") val responseFormat: ResponseFormat,
)

@Serializable
private data class ChatMessage(
    val role: String,
    val content: String,
)

@Serializable
private data class ResponseFormat(val type: String)
