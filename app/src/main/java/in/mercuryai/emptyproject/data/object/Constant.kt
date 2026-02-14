package `in`.mercuryai.chat.data.`object`

import `in`.mercuryai.chat.domain.gemini.GeminiContent
import `in`.mercuryai.chat.domain.gemini.GeminiContent1
import `in`.mercuryai.chat.domain.gemini.GeminiPart
import `in`.mercuryai.chat.domain.model.ChatMessage
import `in`.mercuryai.chat.domain.model.ChatMessageMain
import `in`.mercuryai.chat.domain.model.ChatMessageUi
import `in`.mercuryai.chat.domain.model.Sender
import java.io.File
import kotlin.io.encoding.Base64


import kotlin.io.encoding.ExperimentalEncodingApi

object Constant {

 //  const val GEMINI_API_KEY = "AIzaSyCnoGqYC_YMqhA-OJmuWuzVeje6QpC_KIk"

   const val GEMINI_API_KEY = "AIzaSyDGwcYFKew7G4Q18OR7XQL2eatY5bXVbXI"

    val IMAGE_API_KEY  ="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"

    val OPEN_AI_KEY = "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"


    val GOOGLE_WEB_CLIENT_ID = "69175222787-mbavq5lkk840h4nhqiv5cb9ero2rrcnf.apps.googleusercontent.com"

    val CALLBACK_URL = "https://dlstcpnjmmuaygpxwgps.supabase.co/auth/v1/callback"

    val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRsc3RjcG5qbW11YXlncHh3Z3BzIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkwNjI0MjQsImV4cCI6MjA4NDYzODQyNH0.SpftkuMgZmkw63IcD97tkRGvPb_Ojqqhrdf1XuChZ64"

    val SUPABASE_URL = "https://dlstcpnjmmuaygpxwgps.supabase.co"

   const val IMAGE_KEY = "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"

}

fun List<ChatMessage>.toGeminiContents(): List<GeminiContent> {
    val system = GeminiContent(
        role = "user",
        parts = listOf(
            GeminiPart(
                text = """
You are a helpful AI assistant.
Answer clearly and directly.
Never explain what a good answer should be.
Never critique previous responses.
""".trimIndent()
            )
        )
    )

    val chat = this
        .filter { it.content != null }
        .map { message ->
            GeminiContent(
                role = if (message.role == "assistant") "model" else "user",
                parts = listOf(GeminiPart(text = message.content!!))
            )
        }

    return listOf(system) + chat
}

fun ChatMessageMain.toUi(): ChatMessage {
    return ChatMessage(
        role = if (sender == Sender.USER) "user" else "assistant",
        content = content,
        liked = liked
    )
}


fun ChatMessageMain.toUiWrapper(): ChatMessageUi {
    return ChatMessageUi(
        ui = ChatMessage(
            role = if (sender == Sender.USER) "user" else "assistant",
            content = content,
            liked = liked,
            imageUrl = imageUrl
        ),
        main = this
    )
}

//fun List<ChatMessage>.toGeminiContents(): List<GeminiContent> {
//    return this
//        .filter { it.content != null } // Gemini only understands text
//        .map { message ->
//
//            GeminiContent(
//                role = when (message.role) {
//                    "user" -> "user"
//                    "assistant" -> "model"
//                    else -> "user"
//                },
//                parts = listOf(
//                    GeminiPart(
//                        text = message.content!!
//                    )
//                )
//            )
//        }
//}


fun isImagePrompt(text: String): Boolean {
    val keywords = listOf(
        "generate image",
        "create image",
        "draw",
        "make an image",
        "illustration",
        "picture of",
        "photo of",
        "art of"
    )

    return keywords.any { text.lowercase().contains(it) }
}

@OptIn(ExperimentalEncodingApi::class)
fun saveBase64AndReturnUrl(base64: String): String {
    val bytes = Base64.decode(
        base64,
        android.util.Base64.DEFAULT
    )
    val file = File.createTempFile("img_", ".png")

    file.writeBytes(bytes)

    return file.toURI().toString()
}

