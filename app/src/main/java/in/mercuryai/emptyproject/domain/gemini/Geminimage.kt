package `in`.mercuryai.chat.domain.gemini

data class GeminiVisionRequest(
    val contents: List<VisionContent>
)

data class VisionContent(
    val role: String,
    val parts: List<VisionPart>
)

sealed class VisionPart {
    data class Text(val text: String) : VisionPart()
    data class InlineData(val inlineData: InlineImage) : VisionPart()
}

data class InlineImage(
    val mimeType: String = "image/jpeg",
    val data: String
)

