package `in`.mercuryai.chat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.chat.data.remote.ImageApi
import `in`.mercuryai.chat.data.remote.OpenAiApi
import `in`.mercuryai.chat.data.repository.AiRepositoryImpl
import `in`.mercuryai.chat.data.repository.AuthRepositoryImpl
import `in`.mercuryai.chat.data.repository.ChatRepositoryImpl
import `in`.mercuryai.chat.data.repository.NetworkConnectivityObserverImpl
import `in`.mercuryai.chat.domain.repository.AiRepository
import `in`.mercuryai.chat.domain.repository.AuthRepository
import `in`.mercuryai.chat.domain.repository.ChatRepository
import `in`.mercuryai.chat.domain.repository.NetworkConnectivityObserver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = Constant.SUPABASE_URL,
            supabaseKey = Constant.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
        }

    @Provides
    @Singleton
    fun provideAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository = impl

   // /models/gemini-1.5-flash:generateContent
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAiApi(retrofit: Retrofit): OpenAiApi {
        return retrofit.create(OpenAiApi::class.java)
    }


    @Provides
    @Singleton
    fun provideChatRepository(
        impl: AiRepositoryImpl,
    ): AiRepository = impl

    @Provides
    @Singleton
    fun provideChatRepositoryMain(
        impl: ChatRepositoryImpl
    ): ChatRepository = impl

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ) : NetworkConnectivityObserver {
        return NetworkConnectivityObserverImpl(context, scope)
    }






}