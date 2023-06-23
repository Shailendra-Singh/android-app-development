package com.shail_singh.mrello.utils

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import java.io.ByteArrayOutputStream

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
}