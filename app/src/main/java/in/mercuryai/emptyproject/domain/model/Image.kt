package `in`.mercuryai.emptyproject.domain.model

import kotlinx.serialization.Serializable

enum class ImagenModel(val modelCode: String) {
    FAST("imagen-4.0-fast-generate-001"),
    STANDARD("imagen-4.0-generate-001"),
    ULTRA("imagen-4.0-ultra-generate-001")
}



@Serializable
data class GeminiImageRequest(
    val contents: List<GeminiImageContent>
)

@Serializable
data class GeminiImageContent(
    val parts: List<GeminiImagePart>
)

@Serializable
data class GeminiImagePart(
    val text: String
)



@Serializable
data class GeminiImageResponse(
    val candidates: List<GeminiImageCandidate>
)

@Serializable
data class GeminiImageCandidate(
    val content: GeminiImageContentResponse
)

@Serializable
data class GeminiImageContentResponse(
    val parts: List<GeminiImagePartResponse>
)

@Serializable
data class GeminiImagePartResponse(
    val inlineData: GeminiInlineData? = null
)

@Serializable
data class GeminiInlineData(
    val mimeType: String,
    val data: String // base64
)

//
@Serializable
data class GeminiImageRequest1(
    val prompt: GeminiImagePrompt,
    val sampleCount: Int = 1
)

@Serializable
data class GeminiImagePrompt(
    val text: String
)

@Serializable
data class GeminiImageResponse1(
    val images: List<GeminiGeneratedImage>
)

@Serializable
data class GeminiGeneratedImage(
    val image: GeminiImageData
)

@Serializable
data class GeminiImageData(
    val imageBytes: String // base64
)

//

@Serializable
data class ImagenPredictRequest(
    val instances: List<ImagenInstance>,
    val parameters: ImagenParameters? = null
)

@Serializable
data class ImagenInstance(
    val prompt: String
)

@Serializable
data class ImagenParameters(
    val sampleCount: Int = 1
)

@Serializable
data class ImagenPredictResponse(
    val predictions: List<ImagenPrediction>
)

@Serializable
data class ImagenPrediction(
    val bytesBase64Encoded: String
)

// different api

data class ImageGenerateRequest(
    val inputs: String
)

data class ImageGenerateResponse(
    val imageBytes: ByteArray
)





