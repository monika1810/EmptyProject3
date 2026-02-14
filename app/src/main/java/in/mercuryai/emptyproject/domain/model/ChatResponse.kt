package `in`.mercuryai.chat.domain.model

data class ChatResponse(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>
)
