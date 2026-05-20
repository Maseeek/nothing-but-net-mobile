package com.example.nothingbutnetmobile.ui.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    fun getFileFromUri(context: Context, uri: Uri): File? {
        if (uri.scheme == "file") {
            return uri.path?.let { File(it) }
        }
        
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null
        var tempFile: File? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            tempFile = File(context.cacheDir, "temp_video_${System.currentTimeMillis()}.mp4")
            outputStream = FileOutputStream(tempFile)
            
            if (inputStream != null) {
                val buffer = ByteArray(4096)
                var bytesRead = inputStream.read(buffer)
                while (bytesRead != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    bytesRead = inputStream.read(buffer)
                }
            }
            return tempFile
        } catch (e: Exception) {
            println("error writing out file: " + e.message)
            return null
        } finally {
            try {
                inputStream?.close()
            } catch (e: Exception) {
                // no-op
            }
            try {
                outputStream?.close()
            } catch (e: Exception) {
                // no-op
            }
        }
    }

    fun getCacheSize(context: Context): String {
        var size = 0L
        context.cacheDir.listFiles()?.forEach {
            size += it.length()
        }
        
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$size Bytes"
        }
    }

    fun clearCache(context: Context): Boolean {
        return try {
            context.cacheDir.listFiles()?.forEach {
                it.delete()
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
