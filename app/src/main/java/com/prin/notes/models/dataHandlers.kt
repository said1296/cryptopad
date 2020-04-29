package com.prin.notes.models

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private const val format: String = "yyyy-MM-dd_HH-mm-ss"
    @SuppressLint("ConstantLocale")
    private val locale: Locale = Locale.getDefault()

    private fun toString(date: Date): String {
        val formatter = SimpleDateFormat(
            format,
            locale
        )
        return formatter.format(date)
    }

    fun stringToDate(dateString: String) : Date? {
        val formatter = SimpleDateFormat(
            format,
            locale
        )
        return formatter.parse(dateString)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    fun getCurrentDateTimeAsString(): String {
        val date = getCurrentDateTime()
        return toString(date)
    }
}