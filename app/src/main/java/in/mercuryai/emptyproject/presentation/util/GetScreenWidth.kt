package `in`.mercuryai.chat.presentation.util

fun getScreenWidth(): Float {
    return android.content.res.Resources.getSystem().displayMetrics.widthPixels /
            android.content.res.Resources.getSystem().displayMetrics.density
}