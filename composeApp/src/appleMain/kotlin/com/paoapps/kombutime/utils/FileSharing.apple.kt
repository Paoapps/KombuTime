package com.paoapps.kombutime.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
actual fun shareFile(content: String, filename: String, mimeType: String) {
    try {
        // Get the root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        
        if (rootViewController == null) {
            println("No root view controller available")
            return
        }
        
        // Create activity view controller with text content directly
        val activityViewController = UIActivityViewController(
            activityItems = listOf(content),
            applicationActivities = null
        )
        
        // Present the share sheet
        rootViewController.presentViewController(
            activityViewController,
            animated = true,
            completion = null
        )
    } catch (e: Exception) {
        e.printStackTrace()
    }
}