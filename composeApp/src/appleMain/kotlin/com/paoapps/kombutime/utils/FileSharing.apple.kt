package com.paoapps.kombutime.utils

import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.Foundation.NSURL
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.writeToFile

actual fun shareFile(content: String, filename: String, mimeType: String) {
    try {
        // Create temporary file
        val tempDir = NSTemporaryDirectory()
        val filePath = "$tempDir$filename"
        
        // Write content to file
        (content as NSString).writeToFile(
            path = filePath,
            atomically = true,
            encoding = NSUTF8StringEncoding,
            error = null
        )
        
        val fileURL = NSURL.fileURLWithPath(filePath)
        
        // Get the root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        
        // Create activity view controller
        val activityViewController = UIActivityViewController(
            activityItems = listOf(fileURL),
            applicationActivities = null
        )
        
        // Present the share sheet
        rootViewController?.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
