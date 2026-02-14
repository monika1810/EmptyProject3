package `in`.mercuryai.chat.domain.model

data class ImageGenerationRequest(
    val prompt: String,
    val width: Int = 1024,
    val height: Int = 1024,
    val model: String = "stabilityai/stable-diffusion-xl-base-1.0"
)


data class ImageGenerationResult(
    val imageUrl: String
)


data class OpenRouterImageRequest(
    val model: String,
    val prompt: String,
    val width: Int,
    val height: Int
)

data class OpenRouterImageResponse(
    val data: List<ImageData>
)

data class ImageData(
    val url: String
)

///

data class OpenRouterChatRequest(
    val model: String,
    val messages: List<Message>,
    val response_format: ResponseFormat = ResponseFormat()
)

data class Message(
    val role: String="user",
    val content: String
)

data class ResponseFormat(
    val type: String = "image"
)

data class OpenRouterChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessageImage
)

data class ChatMessageImage(
    val content: List<ContentPart>
)

data class ContentPart(
    val type: String,
    val image_url: ImageUrl?
)

data class ImageUrl(
    val url: String
)

//

data class Message5(
    val role: String = "user",
    val content: List<ContentBlock>
)

data class ContentBlock(
    val type: String = "text",
    val text: String
)

data class OpenRouterImageRequest1(
    val model: String = "black-forest-labs/flux-1-schnell",
    val messages: List<Message5>,
    val response_format: ResponseFormat = ResponseFormat()
)

data class ResponseFormat1(
    val type: String = "image"
)





