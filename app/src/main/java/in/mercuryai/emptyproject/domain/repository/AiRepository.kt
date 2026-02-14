package `in`.mercuryai.chat.domain.repository

import android.net.Uri
import `in`.mercuryai.chat.domain.model.ChatMessage
import `in`.mercuryai.chat.domain.model.ImageGenerationRequest
import `in`.mercuryai.chat.domain.model.ImageGenerationResult

interface AiRepository {


    suspend fun generateImage(prompt: String): String
//    suspend fun generateImage(
//        prompt: String,
//        width: Int,
//        height: Int
//    ): ImageGenerationResult
//
//    suspend fun generateImage1(
//        request: ImageGenerationRequest
//    ): ImageGenerationResult

    suspend fun analyzeImageWithQuestion(imagePath: String,question: String): ChatMessage

   // suspend fun generateImage(prompt: String): ChatMessage

    suspend fun send(messages: List<ChatMessage>): ChatMessage
    suspend fun sendMessage(prompt: String): String

    suspend fun sendMessage(messages: List<ChatMessage>): String

  //  suspend fun sendMessages(messages: List<ChatMessage>): String
}