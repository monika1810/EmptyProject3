package `in`.mercuryai.chat.data.remote

import `in`.mercuryai.chat.data.`object`.Constant.IMAGE_KEY
import `in`.mercuryai.chat.domain.model.ImageGenerateRequest
import `in`.mercuryai.chat.domain.model.OpenRouterChatRequest
import `in`.mercuryai.chat.domain.model.OpenRouterChatResponse
import `in`.mercuryai.chat.domain.model.OpenRouterImageRequest
import `in`.mercuryai.chat.domain.model.OpenRouterImageRequest1
import `in`.mercuryai.chat.domain.model.OpenRouterImageResponse
import okhttp3.ResponseBody
import retrofit2.Response
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