package `in`.mercuryai.emptyproject.presentation.home

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import coil.compose.AsyncImage
import `in`.mercuryai.emptyproject.domain.model.ChatMessageMain
import `in`.mercuryai.emptyproject.domain.model.ChatMessageUi
import `in`.mercuryai.chat.presentation.util.SnackbarEvent
import `in`.mercuryai.emptyproject.presentation.component.MercuryAITopAppBar
import `in`.mercuryai.emptyproject.presentation.home.components.ChatInputBar
import `in`.mercuryai.emptyproject.presentation.home.components.MessageList2
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
    selectedModel: String,
    onModelChange: (String) -> Unit,
    onStartListening: ((String) -> Unit) -> Unit,
    onListen: (ChatMessageMain) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val listState = rememberLazyListState()
    LaunchedEffect(key1 = true) {

        Log.d("TAG","UI collecting on: ${this.coroutineContext[CoroutineDispatcher]}")

        snackbarEvent.collect {  event->
            Log.d("Snackbar",event.message)
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
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
            var expanded by remember { mutableStateOf(false) }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {


                Box(
                    modifier = Modifier.background(Color.Gray)
                ) {
                    TextButton(onClick = { expanded = true }) {
                        Text(selectedModel)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {

                        val models = listOf(
                            "gemma-3-1b-it",
                            "gemini-flash-latest",
                            "gemini-flash-lite",
                            "gemini-flash",
                            "claude-3-sonnet"
                        )

                        models.forEach { model ->
                            DropdownMenuItem(
                                text = { Text(model) },
                                onClick = {
                                    expanded = false
                                    onModelChange(model)
                                }
                            )
                        }
                    }
                }
                MercuryAITopAppBar(
                    modifier = Modifier.weight(1f),
                    onSearchClick = onSearchClick,
                )
            }

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

            MessageList2(
                messages = messages,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                selectedModel=selectedModel,
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
