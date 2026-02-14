package `in`.mercuryai.chat.presentation.component

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import `in`.mercuryai.chat.domain.model.Conversation

@Composable
fun HomeDrawer(
    onNewChat: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Mercury AI",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(24.dp))

        NavigationDrawerItem(
            label = { Text("➕ New Chat") },
            selected = false,
            onClick = onNewChat
        )

        Spacer(Modifier.height(12.dp))

        NavigationDrawerItem(
            label = { Text("🚪 Logout") },
            selected = false,
            onClick = onLogout
        )
    }
}

@Composable
fun HomeDrawer(
    conversations: List<Conversation>,
    onConversationClick: (Conversation) -> Unit,
    onNewChat: () -> Unit,
    onLogout: () -> Unit,
    onRenameConversation: (Conversation, String) -> Unit,
    onDeleteConversation: (Conversation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {

        TextButton(onClick = onNewChat) {
            Text("➕ New Chat")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Chats",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(0.8f)
        ) {
            items(
                items = conversations,
                key = { it.id }
            ) { convo ->

                var menuExpanded by remember { mutableStateOf(false) }
                var showRenameDialog by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                var background by remember { mutableStateOf(Color.White) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = LocalIndication.current,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            background = Color.LightGray
                            onConversationClick(convo)
                        }
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = convo.title ?: "New Chat",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Rename") },
                                onClick = {
                                    menuExpanded = false
                                    showRenameDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = {
                                    menuExpanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }

                if (showRenameDialog) {
                    RenameDialog(
                        currentTitle = convo.title.orEmpty(),
                        onConfirm = { newTitle ->
                            onRenameConversation(convo, newTitle)
                            showRenameDialog = false
                        },
                        onDismiss = { showRenameDialog = false }
                    )
                }

                if (showDeleteDialog) {
                    DeleteConfirmDialog(
                        onConfirm = {
                            onDeleteConversation(convo)
                            showDeleteDialog = false
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }
            }
        }

        Divider()



        TextButton(onClick = onLogout) {
            Text("Logout")
        }
    }
}