package `in`.mercuryai.emptyproject.data.remote

import `in`.mercuryai.emptyproject.domain.model.ImageGenerateRequest
import `in`.mercuryai.emptyproject.domain.model.OpenRouterChatResponse
import `in`.mercuryai.emptyproject.domain.model.OpenRouterImageRequest1
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ImageApi {

    @Headers("Content-Type: application/json")
    @POST("/models/runwayml/stable-diffusion-v1-5")
    suspend fun generateImage(
        @Body request: ImageGenerateRequest
    ): ResponseBody


    @POST("v1/chat/completions")
    suspend fun generateImage(
        @Body request: OpenRouterImageRequest1
    ): OpenRouterChatResponse
}