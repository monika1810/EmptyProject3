package `in`.mercuryai.chat.domain.gemini

//data class GeminiRequest(
//    val contents: List<GeminiContent1>
//)

data class GeminiContent1(
    val parts: List<GeminiPart>
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiContentResponse
)

data class GeminiContentResponse(
    val parts: List<GeminiPartResponse>
)

data class GeminiPartResponse(
    val text: String
)


