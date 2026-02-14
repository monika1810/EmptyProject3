package `in`.mercuryai.emptyproject.presentation.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TextToSpeechHelper(context: Context) {

    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                tts?.setSpeechRate(1.0f)
                tts?.setPitch(1.0f)
            }
        }
    }

    fun speak(text: String) {
        if (text.isBlank()) return

        tts?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "AI_REPLY"
        )
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
