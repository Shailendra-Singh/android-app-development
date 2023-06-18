package com.shail_singh.happyplaces.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object StorageOperations {
    fun readBitmapFromStorage(bitmapUri: Uri): Bitmap? {
        try {
            val inputStream = FileInputStream(bitmapUri.toString())
            val thumbnail = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            return thumbnail
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun deleteFileFromStorage(filePath: String): Boolean {
        var result = false
        try {
            var f = File(filePath)
            if (f.exists()) result = f.delete()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result
    }

    fun deleteFileFromStorage(fileUri: Uri) {
        deleteFileFromStorage(fileUri.toString())
    }
}