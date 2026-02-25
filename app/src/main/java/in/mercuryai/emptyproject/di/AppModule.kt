package `in`.mercuryai.emptyproject.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.emptyproject.data.remote.OpenAiApi
import `in`.mercuryai.emptyproject.data.repository.AiRepositoryImpl
import `in`.mercuryai.chat.data.repository.AuthRepositoryImpl
import `in`.mercuryai.emptyproject.data.repository.ChatRepositoryImpl
import `in`.mercuryai.emptyproject.data.repository.NetworkConnectivityObserverImpl
import `in`.mercuryai.emptyproject.domain.repository.AiRepository
import `in`.mercuryai.emptyproject.domain.repository.AuthRepository
import `in`.mercuryai.emptyproject.domain.repository.ChatRepository
import `in`.mercuryai.emptyproject.domain.repository.NetworkConnectivityObserver
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }


    // /models/gemini-1.5-flash:generateContent
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .client(okHttpClient)
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