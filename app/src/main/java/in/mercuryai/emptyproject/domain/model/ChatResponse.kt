package `in`.mercuryai.emptyproject.domain.model

data class ChatResponse(
    val model: String = "gpt-4o-mini",
    val messages: List<ChatMessage>
)
