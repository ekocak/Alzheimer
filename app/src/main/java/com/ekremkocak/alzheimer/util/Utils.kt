package com.ekremkocak.alzheimer.util

import java.text.SimpleDateFormat
import java.util.*

class Utils {
    companion object {
        fun convertTimeToDateString(timeInMillis: Long): String {
            val date = Date(timeInMillis)
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }
    }
}