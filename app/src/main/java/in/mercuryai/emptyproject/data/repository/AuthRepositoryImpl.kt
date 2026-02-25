package `in`.mercuryai.chat.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.emptyproject.domain.repository.AuthRepository
import `in`.mercuryai.emptyproject.presentation.auth.AuthResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.security.MessageDigest
import java.util.UUID

import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
): AuthRepository {

    override fun signUpWithEmail(email: String, password: String): Flow<AuthResponse> = flow{


        supabase.auth.signUpWith(Email) {
            this.email =email
            this.password = password
        }

        emit(AuthResponse.Success)

    }

    override fun signInWithEmail(
        email: String,
        password: String
    ): Flow<AuthResponse> = flow {

        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }

        emit(AuthResponse.Success)
    }


    override  fun signUpWithGoogle(context: Context): Flow<AuthResponse> = flow {
            val rawNonce = UUID.randomUUID().toString()
            val md = MessageDigest.getInstance("SHA-256")
            val hashedNonce = md.digest(rawNonce.toByteArray())
                .joinToString("") { "%02x".format(it) }

            val googleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(Constant.GOOGLE_WEB_CLIENT_ID)
                .setAutoSelectEnabled(false)
                .setFilterByAuthorizedAccounts(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)

            val result = credentialManager.getCredential(context, request)

            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)

            supabase.auth.signInWith(IDToken) {
                idToken = googleIdTokenCredential.idToken
                provider = Google
            }

            emit(AuthResponse.Success)

        }.catch { e ->

        }



    override suspend fun restoreSession() {

    }

    override suspend fun logOut() {
        supabase.auth.signOut()
    }

    override fun currentUserId(): String {
        return supabase.auth.currentSessionOrNull()
            ?.user
            ?.id
            ?: throw IllegalStateException("User not logged in")
    }

    override fun currentSession() = supabase.auth.currentSessionOrNull()



}