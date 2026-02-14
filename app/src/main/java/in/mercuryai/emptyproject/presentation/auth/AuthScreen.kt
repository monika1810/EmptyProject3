package `in`.mercuryai.chat.presentation.auth


import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.chat.presentation.util.SnackbarEvent
import io.github.jan.supabase.SupabaseClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.nio.file.WatchEvent
import java.security.MessageDigest
import java.util.UUID

@Composable
fun AuthScreen1(
    modifier: Modifier = Modifier,
    navigateToHomeScreen: () -> Unit,
    navigateToSignInScreen: () -> Unit,
    navigateToHomeScreen1: () -> Unit,
    snackbarHostState: SnackbarHostState,
    snackbarEvent: Flow<SnackbarEvent>,
) {

    LaunchedEffect(key1 = true) {
        snackbarEvent.collect{event ->
            Log.d("TAG", "AuthScreen: $event")
            snackbarHostState.showSnackbar(
                message = event.message,
                duration = event.duration
            )
        }
    }

    val viewModel = hiltViewModel<AuthViewModel>()

    val context = LocalContext.current


    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        EmailSignUp(
            navigateToHome = navigateToHomeScreen1
        )

        Button(
            onClick = navigateToSignInScreen
        ) {
            Text("Already have an Account?")
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
//                .clickable(
//                    enabled = true,
//                    onClick = { navigateToSignInScreen() }
//                )
                .padding(end = 14.dp, top = 12.dp)
               ,
            text = "Already have an account?",
            textAlign = TextAlign.End
        )

        Button(
            onClick = navigateToHomeScreen
        ) {
            Text("Google")
        }




//        GoogleSignInButton()
//        InsertButton()
    }


}


@Composable
fun EmailSignUp(
    viewModel: AuthViewModel = hiltViewModel<AuthViewModel>(),
    navigateToHome: () -> Unit,
    context: Context = LocalContext.current
) {

    var emailValue by remember {
        mutableStateOf("")
    }

    var passwordValue by remember {
        mutableStateOf("")
    }


    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Email",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = emailValue,
            onValueChange = { newValue ->
                emailValue = newValue
            },
            placeholder = {
                Text(
                    text = "john.doe@example.com",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Password",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextField(
            value = passwordValue,
            onValueChange = { newValue ->
                passwordValue = newValue
            },
            placeholder = {
                Text(
                    text = "Enter your password",
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.colors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }

    Spacer(modifier = Modifier.height(35.dp))

    Button(
        onClick = {

            viewModel
                .signUpWithEmail(
                    emailValue,
                    passwordValue,
                    onSuccess = navigateToHome,
                    onError = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                    }
                ).let {
                    it
                    Log.d("TAG", "EmailSignUp: $it")
                }


//            authManager.signUpWithEmail(emailValue, passwordValue)
//                .onEach { result ->
//                    if (result is AuthResponse.Success) {
//                        Log.d("auth", "Email Success")
//                    } else {
//                        Log.e("auth", "Email Failed")
//                    }
//                }
//                .launchIn(coroutineScope)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Cyan
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Sign up",
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }

}

@Composable
fun InsertButton() {

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    Button(
        onClick = {
            coroutineScope.launch {

            }
        }
    ) {
        Text(text = "Insert a new row")
    }


}

sealed interface AuthResponse {
    data object Success : AuthResponse

    data class Error(val message: String?) : AuthResponse
}


@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()


    val rawNonce = UUID.randomUUID().toString()
    val bytes = rawNonce.toByteArray()
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(bytes)
    val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

    val onClick: () -> Unit = {


        val credentialManager = CredentialManager.create(context)

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(Constant.GOOGLE_WEB_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {

            try {
                val result = credentialManager.getCredential(context, request)

                val credential = result.credential

                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken





                Log.d("GoogleIdToken", googleIdToken)

                Toast.makeText(context, "You are signed in!", Toast.LENGTH_SHORT).show()

            } catch (e: GetCredentialException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()

            }


        }


    }

    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = modifier
    ) {
        Text(text = "Sign in with Google")

    }


}


@Composable
fun signUpWithEmail(
    emailValue: String, passwordValue: String
): Flow<AuthResponse> = flow {

    try {


    } catch (e: Exception) {
        emit(AuthResponse.Error(e.localizedMessage))
    }

}