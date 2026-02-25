package `in`.mercuryai.emptyproject.domain.repository

import android.content.Context
import android.net.Uri
import `in`.mercuryai.emptyproject.domain.model.ChatMessageMain
import `in`.mercuryai.emptyproject.domain.model.Conversation
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ChatRepository {

    suspend fun updateConversationTitle(
        conversationId: String,
        title: String
    )

    fun copyUriToLocalFile(uri: Uri,context: Context): String {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("Cannot open image")

        val file = File(
            context.cacheDir,
            "img_${System.currentTimeMillis()}.jpg"
        )

        file.outputStream().use { output ->
            inputStream.copyTo(output)
        }

        return file.absolutePath
    }


    suspend fun updateMessageLike(
        messageId: String,
        liked: Boolean
    )

    suspend fun deleteConversation(
        conversationId: String
    )

    suspend fun getOrCreateConversation(userId: String): Conversation

    fun observeMessages(conversationId: String): Flow<List<ChatMessageMain>>

    suspend fun sendUserMessage(
        conversationId: String,
        text: String,
        imageUrl: String?
    )

    fun observeConversations(userId: String): Flow<List<Conversation>>

    suspend fun saveAiMessage(
        conversationId: String,
        text: String,
        imageUrl: String? = null,
        modelName:String?=null
    )

    suspend fun createConversation(userId: String): Conversation // 👈 NEW



}