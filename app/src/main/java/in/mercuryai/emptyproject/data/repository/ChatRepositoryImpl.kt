package `in`.mercuryai.emptyproject.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import `in`.mercuryai.emptyproject.domain.model.ChatMessageMain
import `in`.mercuryai.emptyproject.domain.model.Conversation
import `in`.mercuryai.emptyproject.domain.repository.ChatRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.decodeRecord
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject
import kotlin.collections.mapOf

@RequiresApi(Build.VERSION_CODES.O)
class ChatRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : ChatRepository {


    override suspend fun updateConversationTitle(
        conversationId: String,
        title: String
    ) {
        val result = supabase
            .from("conversations")
            .update(
                mapOf(
                    "title" to title,
                    "updated_at" to Instant.now().toString()
                )
            ) {
                filter { eq("id", conversationId) }
            }

        Log.d("SITE", "Title update result: ${result.data}")
    }

    override suspend fun updateMessageLike(
        messageId: String,
        liked: Boolean
    ) {
       val result =   supabase
            .from("messages")
            .update(
                mapOf("liked" to liked)
            ) {
                filter {
                    eq("id", messageId)
                }
            }
        Log.d("SITE", "Like update result: ${result.data}")
    }

    override suspend fun deleteConversation(conversationId: String) {
        // delete messages first (important)
        supabase.from("messages")
            .delete {
                filter { eq("conversation_id", conversationId) }
            }

        // then delete conversation
        supabase.from("conversations")
            .delete {
                filter { eq("id", conversationId) }
            }

    }

    override suspend fun getOrCreateConversation(userId: String): Conversation {
        val existing = supabase
            .from("conversations")
            .select {
                filter {
                    eq("user_id", userId)
                }
                limit(1)
            }
            .decodeList<Conversation>()

        Log.d("TAG", "getOrCreateConversation: $existing")

        return if (existing.isNotEmpty()) {
            existing.first()
        } else {
            supabase
                .from("conversations")
                .insert(
                    mapOf("user_id" to userId)
                ) {
                    select()
                }
                .decodeSingle<Conversation>()
        }
    }


    override fun observeMessages(
        conversationId: String
    ): Flow<List<ChatMessageMain>> = callbackFlow {

        val initial = supabase
            .from("messages")
            .select {
                filter { eq("conversation_id", conversationId) }
                order("created_at", Order.ASCENDING)
            }
            .decodeList<ChatMessageMain>()

        val messages = initial.toMutableList()
        trySend(messages.toList())

        val channel = supabase.channel("messages-$conversationId")

        val job = channel
            .postgresChangeFlow<PostgresAction.Insert>("public") {
                table = "messages"
                filter = "conversation_id=eq.$conversationId"
            }
            .onEach {
                val newMessage = it.decodeRecord<ChatMessageMain>()
                messages.add(newMessage)

                // new list reference → Compose recompose
                trySend(messages.toList())
            }
            .launchIn(this)

        // 🔥 subscribe immediately
        channel.subscribe()

        awaitClose {
            job.cancel()

            // 🚑 suspend call inside coroutine
            launch {
                channel.unsubscribe()
            }
        }
    }


    override suspend fun sendUserMessage(conversationId: String, text: String,imageUrl: String?) {
        val nowIso = Instant.now().toString()
        supabase.from("messages").insert(
            mapOf(
                "conversation_id" to conversationId,
                "sender" to "user",
                "content" to text,
                "created_at" to nowIso,
                "image_url" to imageUrl
            )
        )

        updateConversationTime(conversationId)
    }

    override fun observeConversations(
        userId: String
    ): Flow<List<Conversation>> = flow {
        while (true) {
            val conversations = supabase
                .from("conversations")
                .select {
                    filter { eq("user_id", userId) }
                    order("updated_at", Order.DESCENDING)
                }
                .decodeList<Conversation>()

            emit(conversations)
            delay(1_000) // simple polling (realtime optional later)
        }
    }

    override suspend fun saveAiMessage(
        conversationId: String,
        text: String,
        imageUrl: String?,
        modelName: String?)
    {
        val nowIso = Instant.now().toString()
        supabase.from("messages").insert(
            mapOf(
                "conversation_id" to conversationId,
                "sender" to "ai",
                "content" to text,
                "created_at" to nowIso
            )
        )

        updateConversationTime(conversationId)
    }

    override suspend fun createConversation(userId: String): Conversation {

        val nowIso = Instant.now().toString()

        return supabase
            .from("conversations")
            .insert(
                mapOf(
                    "user_id" to userId,
                    "created_at" to nowIso,
                    "updated_at" to nowIso,
                )
            ) {
                select()
            }
            .decodeSingle<Conversation>()

    }


    suspend fun updateConversationTime(
        conversationId: String
    ) {
        val nowIso = Instant.now().toString()
        supabase
            .from("conversations")
            .update(
                mapOf("updated_at" to nowIso)
            ) {
                filter {
                    eq("id", conversationId)
                }
            }
    }


}


