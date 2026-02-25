package `in`.mercuryai.emptyproject.data.remote

import `in`.mercuryai.emptyproject.data.remote.dto.ChatRequester
import `in`.mercuryai.emptyproject.domain.gemini.GeminiRequest
import `in`.mercuryai.emptyproject.domain.gemini.GeminiResponse2
import `in`.mercuryai.emptyproject.domain.gemini.ImagenRequest
import `in`.mercuryai.emptyproject.domain.gemini.ImagenResponse
import `in`.mercuryai.emptyproject.domain.model.ChatResponse
import `in`.mercuryai.emptyproject.domain.model.ImagenPredictRequest
import `in`.mercuryai.emptyproject.domain.model.ImagenPredictResponse
import retrofit2.http.Body
import retrofit2.http.GET
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
//    @POST("v1beta/models/gemini-2.5-flash-lite:generateContent")
//    suspend fun generateContent1(
//        @Header("key") apiKey: String,
//        @Body body: GeminiRequest
//    ): GeminiResponse

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-flash-latest:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body body: Any
    ): GeminiResponse2

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.5-flash-lite:generateContent")
    suspend fun generateContentFlashLite(
        @Query("key") apiKey: String,
        @Body body: Any
    ): GeminiResponse2



    // Gemini Model : Gemini 2.5 Flash and Gemini 2.5 Flash Lite
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent9(
        @Query("key") apiKey: String,
        @Body body: Any
    ): GeminiResponse2


    // paid one
    @Headers("Content-Type: application/json")
    @POST("v1beta/models/imagen-4.0-fast-generate-001:predict")
    suspend fun generateImage3(
        @Query("key") apiKey: String,
        @Body body: ImagenRequest
    ): ImagenResponse


    // gemini-2.5-flash-image

    @POST("v1/models/imagen-3.0-generate-002:predict")
    suspend fun generateImage(
        @Header("Authorization") auth: String,
        @Body body: ImagenRequest
    ): ImagenResponse

    @POST("v1/models/gemini-2.5-flash-native-audio-latest")
    suspend fun generateImage(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: ImagenPredictRequest
    ): ImagenPredictResponse

    @Headers("Content-Type: application/json")
    @POST("v1beta/models/gemma-3-1b-it:generateContent")
    suspend fun generateGemma(
        @Query("key") apiKey: String,
        @Body body: GeminiRequest
    ): GeminiResponse2


//    @GET("v1beta/models")
//    suspend fun listModels(
//        @Query("key") apiKey: String
//    ): ResponseBody

    @GET("v1beta/models")
    suspend fun listModels(
        @Query("key") apiKey: String
    ): ModelListResponse




}