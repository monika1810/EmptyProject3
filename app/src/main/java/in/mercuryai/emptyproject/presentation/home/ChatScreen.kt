package `in`.mercuryai.chat.presentation.home

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import `in`.mercuryai.chat.domain.model.ChatMessage
import `in`.mercuryai.chat.domain.model.ChatMessageMain
import `in`.mercuryai.chat.domain.model.ChatMessageUi
import `in`.mercuryai.chat.presentation.component.MercuryAITopAppBar
import `in`.mercuryai.chat.presentation.home.components.ChatInputBar
import `in`.mercuryai.chat.presentation.home.components.MessageList
import `in`.mercuryai.chat.presentation.util.SnackbarEvent
import `in`.mercuryai.chat.presentation.util.Surface
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow

@Composable
fun ChatScreen(
    modifier: Modifier,
    messages: List<ChatMessageUi>,
    snackbarHostState: SnackbarHostState,
    snackbarEvent: Flow<SnackbarEvent>,
    onSendMessage: (String,Uri?) -> Unit,
    onSearchClick: () -> Unit,
    onLike: (ChatMessageMain) -> Unit,
    onDislike: (ChatMessageMain) -> Unit,
    onCopy: (ChatMessageMain) -> Unit,
    onShare: (ChatMessageMain) -> Unit,
    onRegenerate: (ChatMessageMain) -> Unit,
    onModelChange: (ChatMessageMain) -> Unit,
    onStartListening: ((String) -> Unit) -> Unit,
    onListen: (ChatMessageMain) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {

        Log.d("TAG","UI collecting on: ${this.coroutineContext[CoroutineDispatcher]}")

        snackbarEvent.collect{
            snackbarHostState.showSnackbar(
                message = it.message,
                duration = it.duration
            )
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    var textFieldValue by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            selectedImageUri = uri
        }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            MercuryAITopAppBar(
                modifier = Modifier,
                onSearchClick = onSearchClick,
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = paddingValues.calculateBottomPadding(),
                    top = paddingValues.calculateTopPadding()
                )
                .background(Color.Gray)
        ) {

            MessageList(
                messages = messages,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                listState = listState,
                onLike = onLike,
                onDislike = onDislike,
                onCopy = onCopy,
                onShare = onShare,
                onRegenerate = onRegenerate,
                onModelChange = onModelChange,
                onListen = onListen
            )

            if (selectedImageUri != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    IconButton(
                        onClick = { selectedImageUri = null },
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove image")
                    }
                }
            }

            ChatInputBar(
                text = textFieldValue,
                onTextChange = { textFieldValue = it },
                onSend = {
                    if (textFieldValue.isNotBlank() || selectedImageUri != null) {
                        onSendMessage(textFieldValue, selectedImageUri)
                        textFieldValue = ""
                        selectedImageUri = null
                        keyboardController?.hide()
                    }
                },
                onMicClick = {
                    onStartListening {
                        textFieldValue = it
                    }
                },
                onAddClick = {
                    imagePickerLauncher.launch("image/*")
                    /* Handle add click */
                },
                modifier = Modifier
                    .fillMaxWidth()
                   .imePadding()
//                    .padding(bottom = 15.dp)
//                    .align(Alignment.End)
            )

        }
    }






}

@Composable
fun ChatBubble1(message: ChatMessage1) {
    val alignment = if (message.isUser) Arrangement.End else Arrangement.Start
    val color = if (message.isUser) Color(0xFFD1E7FF) else Color(0xFFF0F0F0)

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = alignment
    ) {
        Surface(
            color = color,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            message.text?.let { Text(text = it, modifier = Modifier.padding(12.dp)) }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage1) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start

    Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = alignment) {
        if (message.isImageResponse && message.image != null) {
            // Display Generated Image
            Image(
                bitmap = message.image.asImageBitmap(),
                contentDescription = "Generated AI Image",
                modifier = Modifier.size(250.dp).clip(RoundedCornerShape(12.dp))
            )
        } else {
            // Display Text
            Surface(
                color = if (message.isUser) Color(0xFF007AFF) else Color(0xFFE9E9EB),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = message.text ?: "",
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isUser) Color.White else Color.Black
                )
            }
        }
    }
}
