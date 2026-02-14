package `in`.mercuryai.chat.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.mercuryai.chat.domain.model.ImageGenerationRequest
import `in`.mercuryai.chat.domain.repository.AiRepository
import `in`.mercuryai.chat.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: AiRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading

    fun generateImage() {
        viewModelScope.launch {
          val image =  imageRepository.generateImage(
               prompt = "cinematic cyberpunk dog"
           )
            Log.d("TAG", "generateImage: $image")
        }
    }


//    fun generateImageAndSaveToChat(
//        conversationId: String,
//        prompt: String
//    ) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                _loading.value = true
//
//                // 1️⃣ Save user prompt
//                chatRepository.sendUserMessage(
//                    conversationId = conversationId,
//                    text = prompt,
//                    imageUrl = ""
//                )
//
//                // 2️⃣ Generate image
//                val result = imageRepository.generateImage(
//                    prompt = prompt,
//                    width = 1024,
//                    height = 1024
//                )
//
//                Log.d("TAG", "generateImageAndSaveToChat: $result")
//                Log.d("TAG", "generateImageAndSaveToChat: ${result.imageUrl}")
//
//                // 3️⃣ Save AI image message
//                chatRepository.saveAiMessage(
//                    conversationId = conversationId,
//                    text = "🖼️ Generated Image",
//                    imageUrl = result.imageUrl
//                )
//
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                _loading.value = false
//            }
//        }
//    }
}