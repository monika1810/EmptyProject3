package `in`.mercuryai.emptyproject.domain.repository

import android.content.Context
import `in`.mercuryai.emptyproject.presentation.auth.AuthResponse
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    fun currentUserId(): String

    fun signUpWithEmail(email:String,password: String): Flow<AuthResponse>


    fun signInWithEmail(email: String, password: String): Flow<AuthResponse>

    fun signUpWithGoogle(context: Context): Flow<AuthResponse>


    suspend fun restoreSession()

    suspend fun logOut()

    fun currentSession(): UserSession?


}