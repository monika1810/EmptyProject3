package `in`.mercuryai.chat.domain.gemini

data class ImagenRequest(
    val instances: List<ImagenInstance>,
    val parameters: ImagenParameters = ImagenParameters()
)

data class ImagenInstance(
    val prompt: String
)

data class ImagenParameters(
    val sampleCount: Int = 1
)

data class ImagenResponse(
    val predictions: List<ImagenPrediction>
)

data class ImagenPrediction(
    val bytesBase64Encoded: String
)

