package com.appdev.scanner.Utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getCurrentTimeAndDate(time:Long): String {
    val currentDateTime = Date(time)
    val format = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
    return format.format(currentDateTime)
}



//fun formatDateString(): String {
//    return try {
//        val dateString = getCurrentTimeAndDate()
//        val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())
//        val date = inputFormat.parse(dateString)
//        val outputFormat = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
//        outputFormat.format(date)
//    } catch (e: Exception) {
//        "Invalid date"
//    }
//}