package `in`.mercuryai.emptyproject.data.remote.dto

data class Message(
    val annotations: List<Any>,
    val content: String,
    val refusal: Any,
    val role: String
)