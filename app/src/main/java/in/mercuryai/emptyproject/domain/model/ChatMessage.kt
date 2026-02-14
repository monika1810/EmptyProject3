package `in`.mercuryai.chat.domain.model

import android.net.Uri

//data class ChatMessage(
//    val role: String, // "user" | "assistant"
//    val content: String?=null,
//    val imageUrl: String? = null // image
//)

data class ChatMessageUi(
    val ui: ChatMessage,
    val main: ChatMessageMain
)

data class ChatMessage(
    val role: String, // "user" | "assistant"
    val content: String? = null,
    val imageUrl: String? = null,
    val liked: Boolean? = null,
    val modelName: String? = null
)


