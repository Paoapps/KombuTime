package com.paoapps.kombutime.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

private var appContext: Context? = null

fun initFileSharing(context: Context) {
    appContext = context.applicationContext
}

actual fun shareFile(content: String, filename: String, mimeType: String) {
    val context = appContext ?: return

    try {
        // Create a temporary file in cache directory
        val file = File(context.cacheDir, filename)
        file.writeText(content)

        // Get URI using FileProvider
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        // Create share intent
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val chooserIntent = Intent.createChooser(shareIntent, "Export $filename")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
