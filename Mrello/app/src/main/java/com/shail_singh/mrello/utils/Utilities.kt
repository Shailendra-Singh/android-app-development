package com.shail_singh.mrello.utils

import android.app.Activity
import android.content.res.Resources
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.MimeTypeMap
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object Utilities {
    fun getImageBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val inputBytes = stream.toByteArray()
        stream.close()
        return inputBytes
    }

    fun getImageBytes(activity: Activity, contentURI: Uri): ByteArray {
        val stream = activity.contentResolver.openInputStream(contentURI)
        val inputBytes = stream?.readBytes()
        stream?.close()
        return inputBytes!!
    }

    fun generateFileName(
        prefix: String, fileExtension: String
    ): String {
        return "${prefix}${UUID.randomUUID()}.${fileExtension}"
    }

    fun getTodayDateString(): String {
        val date = Date()
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(date)
    }

    fun getFileExtension(uri: Uri?): String? {
        val uriString: String = uri?.toString()!!
        return MimeTypeMap.getFileExtensionFromUrl(uriString)
    }

    fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
    fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}