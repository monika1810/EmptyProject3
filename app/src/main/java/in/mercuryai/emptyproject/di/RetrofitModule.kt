package `in`.mercuryai.emptyproject.di


import `in`.mercuryai.chat.data.`object`.Constant
import `in`.mercuryai.emptyproject.data.remote.ImageApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitModule {

    private const val BASE_URL = "https://openrouter.ai/api/"

    private val authInterceptor = Interceptor { chain ->
        val request = chain.request()
            .newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${Constant.IMAGE_KEY}"
            )
            .addHeader("HTTP-Referer", "in.mercuryai.chat")
            .addHeader("X-Title", "MercuryAI")
            .build()

        chain.proceed(request)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    val imageApi: ImageApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ImageApi::class.java)
}

