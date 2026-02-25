package `in`.mercuryai.emptyproject.domain.gemini

data class GeminiRequest(
    val contents: List<GeminiContent>
)



//data class GeminiResponse(
//    val candidates: List<GeminiCandidate>
//)
//
//data class GeminiCandidate(
//    val content: GeminiContent
//)

data class GeminiContent(
    val role: String ?= null,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String,
    val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    val mimeType: String,
    val data: String // base64
)



