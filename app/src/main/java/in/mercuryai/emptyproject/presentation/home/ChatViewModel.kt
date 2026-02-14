package `in`.mercuryai.chat.presentation.home

import android.content.Context
import android.net.Uri
import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.mercuryai.chat.data.`object`.toUiWrapper
import `in`.mercuryai.chat.domain.model.ChatMessage
import `in`.mercuryai.chat.domain.model.ChatMessageMain
import `in`.mercuryai.chat.domain.model.Conversation
import `in`.mercuryai.chat.domain.model.Sender
import `in`.mercuryai.chat.domain.repository.AiRepository
import `in`.mercuryai.chat.domain.repository.AuthRepository
import `in`.mercuryai.chat.domain.repository.ChatRepository
import `in`.mercuryai.chat.presentation.util.SnackbarEvent
import `in`.mercuryai.emptyproject.presentation.util.TextToSpeechHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


fun ChatMessageMain.toAiMessage(): ChatMessage {
    return ChatMessage(
        role = if (sender == Sender.USER) "user" else "assistant",
        content = content,
    )
}

fun List<ChatMessageMain>.toAiMessages(): List<ChatMessage> =
    map { it.toAiMessage() }
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: AiRepository,
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
): ViewModel() {

    private val _conversationId = MutableStateFlow<String?>(null)
    val messages1: StateFlow<List<ChatMessageMain>> =
        _conversationId
            .filterNotNull()
            .flatMapLatest { conversationId ->
                chatRepository.observeMessages(conversationId)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val messagesUi = messages1
        .map { list ->
            list.map { it.toUiWrapper() }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    private var ttsHelper: TextToSpeechHelper? = null


    private val titledConversations = mutableSetOf<String>()

    private val _userId = MutableStateFlow<String?>(null)

    val conversations: StateFlow<List<Conversation>> =
        _userId
            .filterNotNull()
            .flatMapLatest { userId ->
                chatRepository.observeConversations(userId)
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()



    init {
        createOrLoadConversation()
    }

    private fun createOrLoadConversation() {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId()
                Log.d("TAG", "createOrLoadConversation: $userId")
                _userId.value = userId

                val conversation = chatRepository.getOrCreateConversation(userId)
                Log.d("TAG", "createOrLoadConversation: $conversation")
                _conversationId.value = conversation.id
            } catch (e: Exception) {
                Log.d("TAG", "createOrLoadConversation: $e")
                _snackbarEvent.send(
                    SnackbarEvent(e.message ?: "Failed to load conversation")
                )
            }
        }
    }

    fun openConversation(conversationId: String) {
        _conversationId.value = conversationId
    }


    fun startNewChat() {
        viewModelScope.launch {
            try {
                val userId = authRepository.currentUserId()

                // Create a brand new conversation
                val newConversation = chatRepository.createConversation(userId)

                // Switch the flow to the new conversation
                _conversationId.value = newConversation.id

            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(e.message ?: "Failed to start new chat")
                )
            }
        }
    }

    fun currentConversationId(): String? = _conversationId.value

    fun sendMessage6(
        text: String,
        imageUri: Uri?,
        context: Context
    ) {
        if (text.isBlank() && imageUri == null) return

        val conversationId = _conversationId.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1️⃣ Prepare image (if any)
                val imagePath = imageUri?.let {
                    chatRepository.copyUriToLocalFile(it,context = context) // ⬅️ VERY IMPORTANT
                }

                // 1️⃣ Save USER message (text only)
                chatRepository.sendUserMessage(
                    conversationId = conversationId,
                    text = text.ifBlank { "📷 Image" },
                    imageUrl= imagePath
                )

                val aiReply = if (imagePath != null) {
                    // 🔥 DIRECT vision call (no history scan)
                    repository.analyzeImageWithQuestion(
                        imagePath = imagePath,
                        question = text.ifBlank { "Describe this image" }
                    )
                } else {
                    val history = messages1.value.map {
                        ChatMessage(
                            role = if (it.sender == Sender.USER) "user" else "assistant",
                            content = it.content
                        )
                    } + ChatMessage(role = "user", content = text)

                    repository.send(history)
                }

                // 2️⃣ Save AI reply
                chatRepository.saveAiMessage(
                    conversationId = conversationId,
                    text = aiReply.content ?: "No response"
                )

                // 🔊 SPEAK AI RESPONSE
               ttsHelper?.speak(aiReply.content ?: "")

            } catch (e: Exception) {
                Log.e("SITE", "sendMessage error", e)
                _snackbarEvent.send(
                    SnackbarEvent(e.message ?: "Message failed")
                )
            }
        }
    }


    fun initTts(context: Context) {
        if (ttsHelper == null) {
            ttsHelper = TextToSpeechHelper(context)
        }
    }



//    fun sendMessage5(text: String,imageUri: Uri?) {
//        if (text.isBlank()) return
//
//        val conversationId = _conversationId.value ?: return
//
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // 1️⃣ Save user message
//                chatRepository.sendUserMessage(
//                    conversationId = conversationId,
//                    text = text
//                )
//
//                // 🔥 NEW PART — AUTO TITLE LOGIC
//
//
//                if (!titledConversations.contains(conversationId)) {
//                    val autoTitle = generateTitle(text)
//
//                    Log.d("SITE", "Auto-setting title: $autoTitle")
//
//                    chatRepository.updateConversationTitle(
//                        conversationId = conversationId,
//                        title = autoTitle
//                    )
//
//                    titledConversations.add(conversationId)
//                }
//                // 🔥 END
//
//                // 2️⃣ Build AI context MANUALLY
//                val history = messages1.value
//                    .map {
//                        ChatMessage(
//                            role = if (it.sender == Sender.USER) "user" else "assistant",
//                            content = it.content,
//                        )
//                    }
//                    .toMutableList()
//
//                // 🔥 CRITICAL: add the CURRENT user message
//                history.add(
//                    ChatMessage(
//                        role = "user",
//                        content = text,
//                    )
//                )
//
//                Log.d("SITE", "AI context size: ${history.size}")
//
//                // 3️⃣ Call AI
//              //  val aiReply = repository.send(messages = history)
//
//
//                val lastImagePath = findLastImage(messages1.value.toAiMessages())
//
//                val aiReply = if (lastImagePath != null) {
//                    repository.analyzeImageWithQuestion(
//                        imagePath = lastImagePath,
//                        question = text
//                    )
//                } else {
//                    repository.send(messages = history)
//                }
//
//
//                // 4️⃣ Save AI reply
//                chatRepository.saveAiMessage(
//                    conversationId = conversationId,
//                    text = aiReply.content ?: "No response",
//                    imageUrl = aiReply.imageUrl
//                )
//
//            } catch (e: Exception) {
//                Log.e("SITE", "sendMessage error", e)
//                _snackbarEvent.send(
//                    SnackbarEvent(e.message ?: "Message failed")
//                )
//            }
//        }
//    }

    fun likeMessage(message: ChatMessageMain) {
        viewModelScope.launch {
            Log.d("SITE", "Like message: $message")
            chatRepository.updateMessageLike(
                messageId = message.id,
                liked = true
            )
        }
    }

    fun dislikeMessage(message: ChatMessageMain) {

        viewModelScope.launch {
            Log.d("SITE", "Dislike message: $message")
            chatRepository.updateMessageLike(
                messageId = message.id,
                liked = false
            )
        }
    }


    private fun generateTitle(text: String): String {
        return text
            .replace(Regex("[^A-Za-z0-9 ]"), "")
            .trim()
            .split(" ")
            .take(5)
            .joinToString(" ")
            .replaceFirstChar { it.uppercase() }
    }

    fun updateConversationTitleManually(
        conversationId: String,
        newTitle: String
    ) {
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            try {
                chatRepository.updateConversationTitle(
                    conversationId = conversationId,
                    title = newTitle
                )

                // Prevent auto-title overriding later
                titledConversations.add(conversationId)

                Log.d("SITE", "Title manually updated: $newTitle")

            } catch (e: Exception) {
                Log.e("SITE", "Title update failed", e)
                _snackbarEvent.send(
                    SnackbarEvent("Failed to update title")
                )
            }
        }
    }

    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteConversation(conversationId)

                titledConversations.remove(conversationId)

                // If deleted conversation is currently open
                if (_conversationId.value == conversationId) {
                    _conversationId.value = null
                    createOrLoadConversation()
                }

                Log.d("SITE", "Conversation deleted: $conversationId")

            } catch (e: Exception) {
                Log.e("SITE", "Delete failed", e)
                _snackbarEvent.send(
                    SnackbarEvent("Failed to delete conversation")
                )
            }
        }
    }





}