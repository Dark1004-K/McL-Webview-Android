package com.bdgen.mcwebview.composable

import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.bdgen.mcwebview.core.McWebView


typealias McWebviewDownload = (url:String, userAgent:String, contentDisposition:String, mimeType:String, contentLength:Long) -> Unit
typealias McWebviewReceivedError = (view: WebView?, request: WebResourceRequest?, error: WebResourceError?) -> Boolean
@Composable
fun McWebView(modifier: Modifier = Modifier, onCreated:((McWebView) -> Unit)? = null,
              onDownload:McWebviewDownload? = null,
              onReceivedError: McWebviewReceivedError? = null)
{
    AndroidView(
        modifier = modifier, // Occupy the full available space
        factory = { context -> McWebView(context)
            .apply Init@{
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                Log.d("DarkAngel", "init webview")
                initialize()
                onCreated?.invoke(this)
                if(onDownload != null) setDownloadListener(onDownload)
                if(onReceivedError != null) mcWebViewClient.receivedError = onReceivedError
            }
        }
    )
}