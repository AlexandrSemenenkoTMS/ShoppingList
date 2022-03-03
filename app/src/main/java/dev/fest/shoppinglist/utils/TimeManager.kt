package dev.fest.shoppinglist.utils

import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object TimeManager {
    private const val DEFAULT_TIME_FORMAT = "HH:mm:ss - dd/MM/yyyy"
    fun getCurrentTime(): String {
        val formatter = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.ENGLISH)
        return formatter.format(Calendar.getInstance().time)
    }

    fun getTimeFormat(time: String, defaultPreferences: SharedPreferences): String {

        val defFormatter = SimpleDateFormat(DEFAULT_TIME_FORMAT, Locale.ENGLISH)
        val defDate = defFormatter.parse(time)
        val newFormat = defaultPreferences.getString("time_format_key", DEFAULT_TIME_FORMAT)
        val newFormatter = SimpleDateFormat(newFormat, Locale.getDefault())
        return if (defDate != null) {
            newFormatter.format(defDate)
        } else {
            time
        }
    }
}