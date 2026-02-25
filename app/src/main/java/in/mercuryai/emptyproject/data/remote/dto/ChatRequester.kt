package `in`.mercuryai.emptyproject.data.remote.dto

data class ChatRequester(
    val choices: List<Choice>,
    val created: Int,
    val id: String,
    val model: String,
    val `object`: String,
    val service_tier: String,
    val usage: Usage
)