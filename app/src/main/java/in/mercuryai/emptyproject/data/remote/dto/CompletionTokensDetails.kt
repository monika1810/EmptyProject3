package `in`.mercuryai.chat.data.remote.dto

data class CompletionTokensDetails(
    val accepted_prediction_tokens: Int,
    val audio_tokens: Int,
    val reasoning_tokens: Int,
    val rejected_prediction_tokens: Int
)