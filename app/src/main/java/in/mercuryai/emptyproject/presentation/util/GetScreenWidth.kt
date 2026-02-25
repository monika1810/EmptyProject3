package `in`.mercuryai.emptyproject.presentation.util

import android.content.res.Resources

fun getScreenWidth(): Float {
    return Resources.getSystem().displayMetrics.widthPixels /
            Resources.getSystem().displayMetrics.density
}