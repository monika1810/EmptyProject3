package `in`.mercuryai.chat.data.remote

import `in`.mercuryai.chat.data.`object`.Constant.GEMINI_API_KEY
import `in`.mercuryai.chat.data.`object`.Constant.IMAGE_KEY
import `in`.mercuryai.chat.data.remote.dto.ChatRequester
import `in`.mercuryai.chat.domain.gemini.GeminiRequest
import `in`.mercuryai.chat.domain.gemini.GeminiResponse
import `in`.mercuryai.chat.domain.gemini.ImagenRequest
import `in`.mercuryai.chat.domain.gemini.ImagenResponse
import `in`.mercuryai.chat.domain.model.ChatResponse
import `in`.mercuryai.chat.domain.model.GeminiImageRequest
import `in`.mercuryai.chat.domain.model.GeminiImageRequest1
import `in`.mercuryai.chat.domain.model.GeminiImageResponse
import `in`.mercuryai.chat.domain.model.ImageGenerateRequest
import `in`.mercuryai.chat.domain.model.ImagenPredictRequest
import `in`.mercuryai.chat.domain.model.ImagenPredictResponse
import `in`.mercuryai.chat.domain.model.OpenRouterImageRequest
import `in`.mercuryai.chat.domain.model.OpenRouterImageResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenAiApi {

    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") auth: String,
        @Body body: ChatResponse
    ): ChatRequester




//    @Headers("Content-Type: application/json")
//    @POST("v1beta/models/gemini-1.5-flash:generateContent")
//    suspend fun generateContent(
//        @Header("key") apiKey: String,
//        @Body body: GeminiRequest
//    ): GeminiResponse

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body body: Any
    ): GeminiResponse

   // gemini-2.5-flash-image

    @POST("v1/models/imagen-3.0-generate-002:predict")
    suspend fun generateImage(
        @Header("Authorization") auth: String,
        @Body body: ImagenRequest
    ): ImagenResponse

    @POST("v1/models/{model}:predict")
    suspend fun generateImage(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: ImagenPredictRequest
    ): ImagenPredictResponse




}