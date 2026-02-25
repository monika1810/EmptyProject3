package `in`.mercuryai.emptyproject.domain.repository

import `in`.mercuryai.emptyproject.domain.model.ChatMessage

interface AiRepository {

    suspend fun sendMessage(
        userId: String,
        messages: List<ChatMessage>,
        selectedModel: String
    ): ChatMessage


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

    suspend fun send(
        messages: List<ChatMessage>
    ): ChatMessage
//    suspend fun sendMessage(prompt: String): String

    suspend fun sendMessage(messages: List<ChatMessage>): String

  //  suspend fun sendMessages(messages: List<ChatMessage>): String
}