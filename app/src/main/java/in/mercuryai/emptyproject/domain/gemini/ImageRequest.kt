package `in`.mercuryai.emptyproject.domain.gemini

data class ImagenRequest(
    val instances: List<Instance>,
    val parameters: Parameters? = null
)

data class Instance(
    val prompt: String
)

data class Parameters(
    val sampleCount: Int = 1
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

