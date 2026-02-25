package `in`.mercuryai.emptyproject.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import `in`.mercuryai.emptyproject.domain.model.ChatMessage
import `in`.mercuryai.emptyproject.domain.model.ChatMessageMain
import `in`.mercuryai.emptyproject.domain.model.ChatMessageUi
import java.io.File


@Composable
fun MessageList2(
    messages: List<ChatMessageUi>,
    modifier: Modifier = Modifier,
    listState: LazyListState,
    onLike: (ChatMessageMain) -> Unit,
    onDislike: (ChatMessageMain) -> Unit,
    onCopy: (ChatMessageMain) -> Unit,
    onShare: (ChatMessageMain) -> Unit,
    onRegenerate: (ChatMessageMain) -> Unit,
    onModelChange: (String) -> Unit,
    onListen: (ChatMessageMain) -> Unit,
    selectedModel: String
) {


    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        items(messages) { message ->
            ChatBubble(
                message = message.ui,
                messageMain = message.main,
                onCopy = onCopy,
                selectedModel=selectedModel,
                onLike = onLike,
                onDislike = onDislike,
                onShare = onShare,
                onRegenerate = onRegenerate,
                onModelChange = onModelChange,
                onListen = onListen
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}





@Composable
fun ChatBubble(
    message: ChatMessage,
    messageMain: ChatMessageMain,
    selectedModel: String,
    onCopy: (ChatMessageMain) -> Unit,
    onLike: (ChatMessageMain) -> Unit,
    onDislike: (ChatMessageMain) -> Unit,
    onShare: (ChatMessageMain) -> Unit,
    onRegenerate: (ChatMessageMain) -> Unit,
    onModelChange: (String) -> Unit,
    onListen: (ChatMessageMain) -> Unit
) {
    val isUser = message.role == "user"
    var showActions by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { showActions = !showActions }
                )
            },
        horizontalAlignment = if (isUser)
            Alignment.End else Alignment.Start
    ) {

        // 💬 Bubble
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) Color(0xFFDCF8C6) else Color(0xFFF1F1F1),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
            ) {

                message.imageUrl?.let { path ->
                    AsyncImage(
                        model = File(path),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                }


                message.content?.let {
                    Text(it, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        // ⚡ ACTIONS (assistant only)
        if (!isUser) {
            MessageActionsRow(
                message1 = message,
                message = messageMain,
                selectedModel=selectedModel,
                onCopy = onCopy,
                onLike = onLike,
                onDislike = onDislike,
                onShare = onShare,
                onListen = onListen,
                onRegenerate = onRegenerate,
                onModelChange = onModelChange
            )
        }
    }
}



@Composable
fun MessageActionsRow(
    message1: ChatMessage,
    message: ChatMessageMain,
    selectedModel: String,
    onCopy: (ChatMessageMain) -> Unit,
    onLike: (ChatMessageMain) -> Unit,
    onDislike: (ChatMessageMain) -> Unit,
    onShare: (ChatMessageMain) -> Unit,
    onRegenerate: (ChatMessageMain) -> Unit,
    onModelChange: (String) -> Unit,
    onListen: (ChatMessageMain) -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            onCopy(message)
        }) {
            Icon(Icons.Default.Add, null)
        }

        IconButton(onClick = { onLike(message) }) {
            Icon(
                Icons.Default.ThumbUp,
                null,
                tint = if (message.liked == true)
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onDislike(message) }) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                null,
                tint = if (message.liked == false)
                    MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = { onShare(message) }) {
            Icon(Icons.Default.Share, null)
        }

        IconButton(onClick = {
            onListen(message)
         //   onRegenerate(message)
        }) {
            Icon(Icons.Default.Refresh, null)
        }

        Text(
            text =selectedModel ?: "GPT-4",
            fontSize = 12.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp)

        )
    }
}





@Composable
fun ChatInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .background(
                color = Color(0xFF1E1E1E),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // ➕ Add Button
        IconButton(onClick = onAddClick) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = Color.White
            )
        }

        // ✍️ Text Field
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 16.sp
            ),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        "Ask Mercury AI…",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        )

        // 🎤 or ➤ Send
        IconButton(
            onClick = {
                if (text.isBlank()) onMicClick()
                else onSend()
            }
        ) {
            Icon(
                imageVector = if (text.isBlank())
                    Icons.Default.ShoppingCart
                else
                    Icons.Default.Send,
                contentDescription = "Send",
                tint = Color.White
            )
        }
    }
}

@Composable
fun ChatInputBar2(
    modifier: Modifier = Modifier,
    onSend: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(end = 10.dp, top = 5.dp, bottom = 60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onSearchClick) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color.Gray
            )
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("Ask anything…") },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF1C1C1E),
                unfocusedContainerColor = Color(0xFF1C1C1E),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
        )

        IconButton(onClick = {
            if (text.isNotBlank()) {
                onSend(text)
                text = ""
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = Color(0xFF1E88E5)
            )
        }
    }
}

@Composable
fun MessageActionsRow(
    message: ChatMessageMain,
    onCopy: () -> Unit,
    onLike: () -> Unit,
    onDislike: () -> Unit
) {
    Row(
        modifier = Modifier.padding(top = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        IconButton(onClick = onCopy) {
            Icon(Icons.Default.Add, contentDescription = "Copy")
        }

        IconButton(onClick = onLike) {
            Icon(
                imageVector = Icons.Default.ThumbUp,
                contentDescription = "Like",
                tint = if (message.liked == true)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onDislike) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Dislike",
                tint = if (message.liked == false)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChatBubble5(message: ChatMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    color = if (isUser) Color(0xFF1E88E5) else Color(0xFF1C1C1E),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
        ) {

            // TEXT (primary)
            message.content?.let {
                Text(
                    text = it,
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }

            // 🔮 FUTURE:
            //  if (message.imageUrl != null) Image(...)
            // if (message.audioUrl != null) AudioPlayer(...)
        }
    }
}


@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isUser) Color(0xFFDCF8C6) else Color(0xFFF1F1F1),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
            ) {

                // 🖼 Image
                message.imageUrl?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Generated image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // 📝 Text
                message.content?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


