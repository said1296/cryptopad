package com.prin.notes.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent


class EditTextDetectKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : androidx.appcompat.widget.AppCompatEditText (context, attrs) {
    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            dispatchKeyEvent(event)
            return false
        }
        return super.onKeyPreIme(keyCode, event)
    }
}
