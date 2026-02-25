package `in`.mercuryai.emptyproject.data.`object`

sealed class AiModel(val modelName: String) {

    object Gemma3_1B : AiModel("gemma-3-1b-it")
    object GeminiFlash : AiModel("gemini-2.5-flash")
    object GeminiFlashLite : AiModel("gemini-2.5-flash-lite")
    object GeminiFlashLatest : AiModel("gemini-flash-latest")

    companion object {
        fun from(name: String): AiModel {
            return when (name) {
                "gemma-3-1b-it" -> Gemma3_1B
                "gemini-2.5-flash" -> GeminiFlash
                "gemini-2.5-flash-lite"-> GeminiFlashLite
                "gemini-flash-latest" -> GeminiFlashLatest
                else -> GeminiFlash
            }
        }
    }
}
