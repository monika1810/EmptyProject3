package `in`.mercuryai.emptyproject.presentation.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import `in`.mercuryai.emptyproject.presentation.auth.AuthState
import `in`.mercuryai.emptyproject.presentation.auth.AuthViewModel

import `in`.mercuryai.emptyproject.presentation.home.ChatViewModel

import `in`.mercuryai.chat.presentation.navigation.Routes
import `in`.mercuryai.emptyproject.presentation.util.TextToSpeechHelper
import `in`.mercuryai.chat.presentation.util.VoiceToTextHelper
import `in`.mercuryai.emptyproject.presentation.auth.AuthScreen2
import `in`.mercuryai.emptyproject.presentation.auth.SignInScreen
import `in`.mercuryai.emptyproject.presentation.component.HomeDrawer
import `in`.mercuryai.emptyproject.presentation.home.ChatScreen
import kotlinx.coroutines.launch

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@Composable
fun SetUpNavGraph3(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackbarHostState: SnackbarHostState
) {


    NavHost(
        navController = navController,
        startDestination = Routes.Splash
    ) {


        composable<Routes.Splash> {

            val viewModel = hiltViewModel<AuthViewModel>()
            val authState by viewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when (authState) {
                    AuthState.Authenticated -> {
                        navController.navigate(Routes.HomeScreen) {
                            popUpTo<Routes.Splash> { inclusive = true }
                        }
                    }

                    AuthState.Unauthenticated -> {
                        navController.navigate(Routes.AuthScreen) {
                            popUpTo<Routes.Splash> { inclusive = true }
                        }
                    }

                    AuthState.Loading -> Unit
                }
            }

            // Optional: show loading UI
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }


        composable<Routes.AuthScreen> {

            val viewModel = hiltViewModel<AuthViewModel>()

            val context = LocalContext.current



            AuthScreen2(

                modifier = modifier,
                navigateToHomeScreen = {
                    viewModel.signUpWithGoogle(
                        context,
                        onSuccess = {
                            navController.navigate(Routes.HomeScreen) {
                                popUpTo<Routes.AuthScreen> {
                                    inclusive = true
                                }
                            }
                        },
                        onError = {

                        }
                    )
                },
                navigateToSignInScreen = {
                    navController.navigate(Routes.SignInScreen)
                },
                navigateToHomeScreen1 = {
                    navController.navigate(Routes.HomeScreen)
                },
                snackbarHostState = snackbarHostState,
                snackbarEvent = viewModel.snackbarEvent
            )
        }

        composable<Routes.HomeScreen> {

            val context = LocalContext.current
            val authViewModel = hiltViewModel<AuthViewModel>()
            val chatViewModel = hiltViewModel<ChatViewModel>()
      //      val imageViewModel = hiltViewModel<ImageViewModel>()

            val messages by chatViewModel.messages1.collectAsState()

            val messages1 by chatViewModel.messagesUi.collectAsStateWithLifecycle()

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            val conversations by chatViewModel.conversations.collectAsStateWithLifecycle()


            LaunchedEffect(Unit) {
                chatViewModel.initTts(context)
            }

            val ttsHelper = remember {
                TextToSpeechHelper(context)
            }



            val selectedModel by chatViewModel.selectedModel.collectAsStateWithLifecycle()




            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    HomeDrawer(
                        conversations = conversations,
                        onConversationClick = {
                            chatViewModel.openConversation(it.id)
                            scope.launch { drawerState.close() }
                        },
                        onNewChat = {
                            chatViewModel.startNewChat()
                            scope.launch {
                                drawerState.close()
                            }
                        },
                        onLogout = {
                            authViewModel.logout(
                                onSuccess = {
                                    navController.navigate(Routes.AuthScreen) {
                                        popUpTo<Routes.HomeScreen> {
                                            inclusive = true
                                        }
                                    }
                                },
                                onError = {
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onRenameConversation = { convo, newTitle ->
                            chatViewModel.updateConversationTitleManually(
                                convo.id,
                                newTitle
                            )
                        },
                        onDeleteConversation = { convo ->
                            chatViewModel.deleteConversation(convo.id)
                        }
                    )
                }
            ) {
                ChatScreen(
                    modifier = modifier,
                    messages = messages1,
                    snackbarHostState = snackbarHostState,
                    snackbarEvent = chatViewModel.snackbarEvent,
                    onSendMessage = { text, imageUri ->

                        val finalText = text.trim()
                        if (finalText.isBlank() && imageUri == null) return@ChatScreen

                        Log.d("TAG", "HomeScreen: $finalText")

                        when {
                            // 🖼️ IMAGE GENERATION MODE
                            finalText.startsWith("/image", ignoreCase = true) -> {
                                val prompt = finalText.removePrefix("/image").trim()
                                if (prompt.isNotEmpty()) {
                                    Log.d("TAG", "HomeScreen: $prompt")

                                  //  imageViewModel.generateImage(
//                                        conversationId = chatViewModel.currentConversationId() ?: return@ChatScreen,
//                                        prompt = prompt
                                    //)
                                }
                            }

                            // 📷 IMAGE + QUESTION (VISION)
                            imageUri != null -> {
                                chatViewModel.sendMessage6(
                                    text = finalText,
                                    imageUri = imageUri,
                                    context = context
                                )
                            }

                            // 💬 NORMAL CHAT
                            else -> {
                                chatViewModel.sendMessage6(
                                    text = finalText,
                                    imageUri = null,
                                    context = context
                                )
                            }
                        }
                    },
                    onSearchClick = {
                        scope.launch { drawerState.open() } // 👈 OPEN DRAWER
                    },
                    onLike = {
                        chatViewModel.likeMessage(it)
                    },
                    onDislike = {
                        chatViewModel.dislikeMessage(it)
                    },
                    onCopy = { message ->
                        val clipboard =
                            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(
                            ClipData.newPlainText("chat", message.content)
                        )
                        Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                    },
                    onShare = { message ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, message.content)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                    },
                    onRegenerate = {
                        chatViewModel.sendMessage6(it.content, null, context)
                    },
                    selectedModel=selectedModel,
                    onModelChange = {
                        chatViewModel.changeModel(it)
                    },
                    onStartListening = { onResult ->
                        VoiceToTextHelper(
                            context = context,
                            onResult = onResult,
                            onError = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        ).startListening()
                    },
                    onListen = {
                       ttsHelper.speak(text = it.content)
                    }
                )
            }
        }

//        composable<Routes.HomeScreen> {
//
//            val context = LocalContext.current
//            val viewModel1 = hiltViewModel<AuthViewModel>()
//
//            val viewModel = hiltViewModel<ChatViewModel>()
//
//            val messages by viewModel.messages1.collectAsState()
//
//
//            ChatScreen(
//                modifier= modifier,
//                messages = messages.toAiMessages(),
//                snackbarHostState = snackbarHostState,
//                onSendMessage = viewModel::sendMessage5,
//                onSearchClick = {
//                    viewModel1.logout(
//                        onSuccess = {
//                            navController.navigate(Routes.AuthScreen) {
//                                popUpTo<Routes.HomeScreen> {
//                                    inclusive = true
//                                }
//                            }
//                        },
//                        onError = {
//                            Toast.makeText(context, "Eror", Toast.LENGTH_SHORT).show()
//                            Log.d("TAG", "HomeScreen: $it")
//                        }
//                    )
//                },
//                snackbarEvent = viewModel.snackbarEvent
//            )
//
//        }

        composable<Routes.SignInScreen> {

            SignInScreen(
                navigateToHomeScreen = {
                    navController.navigate(Routes.HomeScreen)
                },
                navigateToSignUpScreen = {

                },
            )

        }


    }


}