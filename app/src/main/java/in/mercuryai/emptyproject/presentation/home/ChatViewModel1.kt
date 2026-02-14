package `in`.mercuryai.chat.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

import `in`.mercuryai.chat.data.`object`.Constant.GEMINI_API_KEY
import kotlinx.coroutines.launch

data class ChatMessage1(
    val text: String? = null,
    val image: android.graphics.Bitmap? = null,
    val isUser: Boolean,
    val isImageResponse: Boolean = false
)

class ChatViewModel1 : ViewModel() {
    // Replace with your actual API Key from Google AI Studio


    // Use the model name you specified
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash", // Or gemini-2.5-flash if available in your region
        apiKey = GEMINI_API_KEY
    )



    // State: List of messages
    val messages = mutableStateListOf<ChatMessage1>()

    fun sendMessage(userPrompt: String) {
        if (userPrompt.isBlank()) return

        // Add user message to UI
        messages.add(ChatMessage1(userPrompt, isUser = true))

        viewModelScope.launch {
            try {
                // Call Gemini API
                val response = generativeModel.generateContent(
                    content { text(userPrompt) }
                )

                // Add Gemini's response to UI
                response.text?.let {
                    messages.add(ChatMessage1(it, isUser = false))
                }
            } catch (e: Exception) {
                messages.add(ChatMessage1("Error: ${e.localizedMessage}", isUser = false))
            }
        }
    }






//    private suspend fun generateImage(prompt: String) {
//        try {
//            val response = imageModel.generateImages(prompt)
//            val bitmap = response.images.first().asBitmap()
//            messages.add(ChatMessage1(image = bitmap, isUser = false, isImageResponse = true))
//        } catch (e: Exception) {
//            messages.add(ChatMessage1(text = "Failed to generate image: ${e.message}", isUser = false))
//        }
//    }
}