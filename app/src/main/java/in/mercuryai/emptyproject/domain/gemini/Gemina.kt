package `in`.mercuryai.emptyproject.domain.gemini

//data class GeminiRequest(
//    val contents: List<GeminiContent1>
//)

///


data class GeminiResponse2(
    val candidates: List<Candidate>?,
    val usageMetadata: UsageMetadata?
)

data class Candidate(
    val content: ContentResponse?,
    val finishReason: String?
)

data class ContentResponse(
    val parts: List<PartResponse>?
)

data class PartResponse(
    val text: String?
)

data class UsageMetadata(
    val promptTokenCount: Int?,
    val candidatesTokenCount: Int?,
    val totalTokenCount: Int?
)



