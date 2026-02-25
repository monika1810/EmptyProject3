package `in`.mercuryai.emptyproject.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class AllChats(
    val allConversations: List<Conversation>
)

@Serializable
data class Conversation(
    val id: String,
    @SerialName("created_at") val createdAt: String,
    @SerialName("user_id")  val userId: String,
     val title: String?=null,
   @SerialName("updated_at") val updatedAt: String?=null
)

@Serializable
data class ChatMessageMain(
    val id: String,
    @SerialName("conversation_id") val conversationId: String,
    val sender: Sender,
    val content: String,
    @SerialName("created_at") val timestamp: String,
    val liked: Boolean? = null,
    @SerialName("image_url")
    val imageUrl: String? = null // ✅
)

enum class Sender {
  @SerialName("user")  USER,
  @SerialName("ai")  AI
}