package `in`.mercuryai.emptyproject.presentation.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.mercuryai.emptyproject.domain.model.UserProfile
import `in`.mercuryai.emptyproject.domain.repository.AuthRepository
import `in`.mercuryai.chat.presentation.util.SnackbarEvent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val supabase: SupabaseClient
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState = _authState.asStateFlow()

    private val _snackbarEvent = Channel<SnackbarEvent>()
    val snackbarEvent = _snackbarEvent.receiveAsFlow()

    init {
        observeAuthState()
    }

    private fun observeAuthState() {
        viewModelScope.launch {
            supabase.auth.sessionStatus.collect { status ->
                _authState.value = when (status) {
                    is SessionStatus.Authenticated -> AuthState.Authenticated

                    is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated

                    is SessionStatus.LoadingFromStorage -> AuthState.Loading

                    SessionStatus.LoadingFromStorage -> AuthState.Loading
                    SessionStatus.NetworkError -> AuthState.Loading
                }
            }
        }
    }

    fun signInWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
             authRepository.signInWithEmail(
                 email = email,
                 password = password
             ).collect {
                 if (it is AuthResponse.Success) {


                     Log.d("TAG", "LogInWithEmail: $it")
                     onSuccess()
                 } else {
                     _snackbarEvent.send(
                         SnackbarEvent(it.toString())
                     )
                     onError(it.toString())
                 }
             }
        }

    }

    fun signUpWithEmail(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {
            authRepository.signUpWithEmail(
                email = email,
                password = password
            ).collect { it ->


                if (it is AuthResponse.Success) {
                    Log.d("TAG", "signUpWithEmail: $it")

                    onSuccess()

                } else {
                    _snackbarEvent.send(
                        SnackbarEvent(it.toString())
                    )
                    Log.d("TAG", "signUpWithEmail: Failed")
                    onError(it.toString())
                }
            }

        }
    }

//    fun signUpWithGoogle(
//        context: Context,
//        onError: (String) -> Unit
//    ) {
//        viewModelScope.launch {
//            authRepository.signUpWithGoogle(context).collect {
//                if (it is AuthResponse.Error) {
//                    onError(it.message ?: "Auth failed")
//                }
//                // ✅ NO navigation here
//                // authState will update automatically
//            }
//        }
//    }

    fun signUpWithGoogle(
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        viewModelScope.launch {
            authRepository.signUpWithGoogle(context).collect { it ->

     //           when (it) {
   //                 is AuthResponse.Success -> checkUser() // 🔥 triggers navigation
 //                   is AuthResponse.Error -> onError(it.message ?: "Unknown error")
//                }
                if (it is AuthResponse.Success) {
                    val session = supabase.auth.currentSessionOrNull()
                    val user = session?.user

                    Log.d("TAG", "User: $user")

                    if (user != null) {

                        val profile = UserProfile(
                            id = user.id,
                            email = user.email,
                            username = user.userMetadata?.get("name")?.jsonPrimitive?.content,
                            avatar_url = user.userMetadata?.get("avatar_url")?.jsonPrimitive?.content
                        )

//                        supabase.postgrest["profiles"]
//                            .upsert(profile)  // 🔥 prevents duplicate insert
                         onSuccess()
                    } else {
                        onError("User is null")
                    }
                } else {
                    _snackbarEvent.send(
                        SnackbarEvent(it.toString())
                    )
                    onError(it.toString())
                }
            }
        }
    }


    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.logOut()
                onSuccess()
            } catch (e: Exception) {
                _snackbarEvent.send(
                    SnackbarEvent(e.message.toString())
                )
                onError(e.message ?: "Logout failed")
            }
        }
    }



}