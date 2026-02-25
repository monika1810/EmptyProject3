package `in`.mercuryai.emptyproject.data.remote.dto

data class Choice(
    val finish_reason: String,
    val index: Int,
    val logprobs: Any,
    val message: Message
)