package `in`.mercuryai.chat.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.google.ai.client.generativeai.type.Content
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.chat.data.`object`.isImagePrompt
import `in`.mercuryai.chat.data.`object`.saveBase64AndReturnUrl
import `in`.mercuryai.chat.data.`object`.toGeminiContents
import `in`.mercuryai.chat.data.remote.ImageApi
import `in`.mercuryai.chat.data.remote.OpenAiApi
import `in`.mercuryai.chat.di.RetrofitModule.imageApi
import `in`.mercuryai.chat.domain.gemini.GeminiContent
import `in`.mercuryai.chat.domain.gemini.GeminiContent1
import `in`.mercuryai.chat.domain.gemini.GeminiPart
import `in`.mercuryai.chat.domain.gemini.GeminiRequest
import `in`.mercuryai.chat.domain.gemini.GeminiResponse
import `in`.mercuryai.chat.domain.gemini.GeminiVisionRequest
import `in`.mercuryai.chat.domain.gemini.ImagenInstance
import `in`.mercuryai.chat.domain.gemini.ImagenRequest
import `in`.mercuryai.chat.domain.gemini.InlineImage
import `in`.mercuryai.chat.domain.gemini.VisionContent
import `in`.mercuryai.chat.domain.gemini.VisionPart
import `in`.mercuryai.chat.domain.model.ChatMessage
import `in`.mercuryai.chat.domain.model.ChatResponse
import `in`.mercuryai.chat.domain.model.ContentBlock
import `in`.mercuryai.chat.domain.model.GeminiImageContent
import `in`.mercuryai.chat.domain.model.GeminiImagePart
import `in`.mercuryai.chat.domain.model.GeminiImagePrompt
import `in`.mercuryai.chat.domain.model.GeminiImageRequest
import `in`.mercuryai.chat.domain.model.GeminiImageRequest1
import `in`.mercuryai.chat.domain.model.ImageGenerateRequest
import `in`.mercuryai.chat.domain.model.ImageGenerationRequest
import `in`.mercuryai.chat.domain.model.ImageGenerationResult
import `in`.mercuryai.chat.domain.model.ImagenModel
import `in`.mercuryai.chat.domain.model.ImagenParameters
import `in`.mercuryai.chat.domain.model.ImagenPredictRequest
import `in`.mercuryai.chat.domain.model.Message
import `in`.mercuryai.chat.domain.model.Message5
import `in`.mercuryai.chat.domain.model.OpenRouterChatRequest
import `in`.mercuryai.chat.domain.model.OpenRouterImageRequest
import `in`.mercuryai.chat.domain.model.OpenRouterImageRequest1
import `in`.mercuryai.chat.domain.repository.AiRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.currentCoroutineContext
import retrofit2.http.Part
import java.io.File
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val api: OpenAiApi,
    @ApplicationContext private val context: Context
): AiRepository {

//    override suspend fun send(messages: List<ChatMessage>): ChatMessage {
//        val lastUserMessage = messages.last().content ?: ""
//        Log.d("LastUser",lastUserMessage)
//
//        Log.d("TAG","Repository Emitting on : ${currentCoroutineContext()[CoroutineDispatcher]}")
//
//        val lastImage = messages.lastOrNull { it.imageUrl != null }
//
//        return if (lastImage != null) {
//            analyzeImageWithQuestion(
//                imagePath = lastImage.imageUrl,
//                question = lastUserMessage
//            )
//        } else {
//            generateText(messages)
//        }
//
//    }

//    override suspend fun generateImage(
//        prompt: String,
//        width: Int,
//        height: Int
//    ): ImageGenerationResult {
//
//        val cleanPrompt = prompt.trim()
//
//        val response = imageApi.generateImage(
//            OpenRouterImageRequest(
//                model = "stabilityai/stable-diffusion-xl-base-1.0",
//                prompt = cleanPrompt,
//                width = width,
//                height = height
//            )
//        )
//
//        return ImageGenerationResult(
//            imageUrl = response.data.first().url
//        )
//    }
//
//    override suspend fun generateImage1(request: ImageGenerationRequest): ImageGenerationResult {
//        val response = imageApi.generateImage(
//            OpenRouterImageRequest(
//                model = request.model,
//                prompt = request.prompt,
//                width = request.width,
//                height = request.height
//            )
//        )
//
//        Log.d("TAG", "generateImage1: $response")
//
//        return ImageGenerationResult(
//            imageUrl = response.data.first().url
//        )
//    }


    override suspend fun send(messages: List<ChatMessage>): ChatMessage {

        val lastUserMessage = messages.lastOrNull { it.content != null }?.content.orEmpty()

        val lastImageMessage = messages.lastOrNull { it.imageUrl != null }

        return if (lastImageMessage?.imageUrl != null) {
            analyzeImageWithQuestion(
                imagePath = lastImageMessage.imageUrl,
                question = lastUserMessage
            )
        } else {
            generateText(messages)
        }
    }

    override suspend fun generateImage(prompt: String): String {


        val request = OpenRouterImageRequest1(
            messages = listOf(
                Message5(
                    content = listOf(
                        ContentBlock(text = prompt)
                    )
                )
            )
        )

        val response = imageApi.generateImage(request)




        return response.choices
            .first()
            .message
            .content
            .first { it.type == "image" }
            .image_url!!
            .url
    }


    private fun sanitizeGeminiText(raw: String): String? {
        val text = raw.trim()

        if (text.isBlank()) return null

        // ❌ Block meta / reviewer responses
        val bannedPhrases = listOf(
            "the user prompt was",
            "the ai response was",
            "this is not helpful",
            "a good response would be",
            "here's an example",
            "the assistant should",
            "the model response"
        )

        if (bannedPhrases.any { text.lowercase().contains(it) }) {
            return null
        }

        // ❌ Block ultra-short nonsense
        if (text.length < 5) return null

        return text
    }

    private fun extractGeminiText(response: GeminiResponse): String {
        val rawText = response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.joinToString("") { it.text.orEmpty() }
            .orEmpty()

        return sanitizeGeminiText(rawText)
            ?: throw IllegalStateException("Invalid Gemini response")
    }


    private suspend fun generateText1(messages: List<ChatMessage>): ChatMessage {

        repeat(2) { attempt ->
            val request = GeminiRequest(
                contents = messages
                    .takeLast(10)
                    .toGeminiContents()
            )

            val response = api.generateContent(
                apiKey = Constant.GEMINI_API_KEY,
                body = request
            )

            Log.d("GEMINI_TEXT", response.toString())

            try {
                val text = extractGeminiText(response)
                return ChatMessage(
                    role = "assistant",
                    content = text,
                )
            } catch (e: Exception) {
                Log.w("GEMINI_RETRY", "Attempt $attempt failed")
            }
        }

        // 🚨 FINAL FALLBACK (never meta, never empty)
        return ChatMessage(
            role = "assistant",
            content = "Sorry, I couldn’t generate a proper response. Please try again."
        )
    }

    private suspend fun generateText(messages: List<ChatMessage>): ChatMessage {

        val request = GeminiRequest(
            contents = messages
                .takeLast(10) // safety
                .toGeminiContents()
        )

        val response = api.generateContent(
            apiKey = Constant.GEMINI_API_KEY,
            body = request
        )

        Log.d("GEMINI_TEXT", response.toString())

        val text = response
            .candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "No response"

        Log.d("GEMINI",text)

        try {
           // val text = extractGeminiText(response)
            return ChatMessage(
                role = "assistant",
                content = text,
            )
        } catch (e: Exception) {
            Log.w("GEMINI_RETRY", "Attempt $e failed")
        }

        return ChatMessage(
            role = "assistant",
            content = "Sorry, I couldn’t generate a proper response. Please try again."
        )
    }



    override suspend fun sendMessage(messages: List<ChatMessage>): String {


            val response = api.chatCompletion(
                auth = "Bearer ${Constant.OPEN_AI_KEY}",
                body = ChatResponse(messages = messages)
            )

            val usage = response.usage.total_tokens

             Log.d("TAG", "sendMessage: $response + $usage")

            return response.choices.first().message.content

    }

//    override suspend fun analyzeImageWithQuestion(
//        imagePath: String,
//        question: String
//    ): ChatMessage {
//
//        val base64Image = uriToBase64(imagePath)
//
//        val request = GeminiVisionRequest(
//            contents = listOf(
//                VisionContent(
//                    role = "user",
//                    parts = listOf(
//                        VisionPart.Text(question),
//                        VisionPart.InlineData(
//                            InlineImage(data = base64Image)
//                        )
//                    )
//                )
//            )
//        )
//
//        val response = api.generateContent(
//            apiKey = Constant.GEMINI_API_KEY,
//            body = request
//        )
//
//        val answer = response.candidates
//            .firstOrNull()
//            ?.content
//            ?.parts
//            ?.firstOrNull()
//            ?.text
//            ?: "I couldn’t understand the image."
//
//        return ChatMessage(
//            role = "assistant",
//            content = answer,
//            modelName = "gemini-1.5-flash"
//        )
//    }

    override suspend fun analyzeImageWithQuestion(
        imagePath: String,
        question: String
    ): ChatMessage {

        // ✅ Safety check (VERY important)
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            Log.e("VISION", "Image file not found: $imagePath")
            return ChatMessage(
                role = "assistant",
                content = "I couldn’t find the image. Please try again."
            )
        }

        // ✅ Convert image → Base64
        val base64Image = imageFile
            .readBytes()
            .let {
                Base64.encodeToString(
                    it,
                    Base64.NO_WRAP
                )
            }

        // ✅ Build Gemini Vision request
        val request = GeminiVisionRequest(
            contents = listOf(
                VisionContent(
                    role = "user",
                    parts = listOf(
                        VisionPart.Text(
                            question.ifBlank { "Describe this image" }
                        ),
                        VisionPart.InlineData(
                            InlineImage(
                                data = base64Image,
                                mimeType = "image/png" // or image/jpeg
                            )
                        )
                    )
                )
            )
        )

        // ✅ Call Gemini
        val response = api.generateContent(
            apiKey = Constant.GEMINI_API_KEY,
            body = request
        )

        // ✅ Extract response safely
        val answer = response.candidates
            .firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: "I couldn’t understand the image."

        return ChatMessage(
            role = "assistant",
            content = answer,
        )
    }


    fun uriToBase64(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image")

        val bytes = inputStream.use { it.readBytes() }

        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }





//    override suspend fun generateImage(prompt: String): ChatMessage {
//
////        val request = GeminiImageRequest(
////            contents = listOf(
////                GeminiImageContent(
////                    parts = listOf(
////                        GeminiImagePart(text = prompt)
////                    )
////                )
////            )
////        )
//
//        val responseBody = imageApi.generateImage(
//            request = ImageGenerateRequest(inputs = prompt)
//        )
//
//        Log.d("TAG", "generateImage: $responseBody")
//
//        val bytes = responseBody.bytes()
//        Log.d("TAG", "generateImage: $bytes")
//
//        val imageUrl = saveBytesAndReturnUrl(bytes, context =context )
//        Log.d("TAG", "generateImage: $imageUrl")
//
//
//        return ChatMessage(
//            role = "assistant",
//            imageUrl = imageUrl
//        )
//    }



//    private suspend fun generateImage1(prompt: String): ChatMessage {
//
//        val request = ImagenRequest(
//            instances = listOf(
//                ImagenInstance(prompt = prompt)
//            )
//        )
//        Log.d("TAG", "generateImage: $request")
//
//        val response = api.generateImage(
//            auth = "Bearer ${Constant.GEMINI_API_KEY}",
//            body = request
//        )
//
//        Log.d("GEMINI_IMAGE", response.toString())
//
//        val base64 = response
//            .predictions
//            .firstOrNull()
//            ?.bytesBase64Encoded
//            ?: return ChatMessage(
//                role = "assistant",
//                content = "Image generation failed"
//            )
//
//        val imageUrl = saveBase64AndReturnUrl(base64)
//
//        Log.d("GEMINI_IMAGE_URL", imageUrl.toString())
//
//        return ChatMessage(
//            role = "assistant",
//            imageUrl = imageUrl
//        )
//    }



//    override suspend fun sendMessages(messages: List<ChatMessage>): String {
//
//        val request = GeminiRequest(
//            contents = messages
//                .takeLast(5)
//                .toGeminiContents()
//        )
//
//        val response = api.generateContent(
//            apiKey = Constant.GEMINI_API_KEY,
//            body = request
//        )
//
//        Log.d("GEMINI", response.toString())
//
//        return response.candidates
//            .first()
//            .content
//            .parts
//            .first()
//            .text
//    }

    override suspend fun sendMessage(prompt: String): String {

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text = prompt)
                    )
                )
            )
        )

        val response = api.generateContent(
            apiKey = Constant.GEMINI_API_KEY,
            body = request
        )

        Log.d("GEMINI_RAW", response.toString())

        return response.candidates[0].content.parts[0].text
    }


    fun saveBytesAndReturnUrl(bytes: ByteArray,context: Context): String {


        val file = File(
            context.cacheDir,
            "img_${System.currentTimeMillis()}.png"
        )
        file.writeBytes(bytes)
        return file.absolutePath
    }



}