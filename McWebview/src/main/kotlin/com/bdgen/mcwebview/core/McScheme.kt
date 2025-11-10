package com.bdgen.mcwebview.core
import android.net.Uri
data class McScheme(val name: String, val action: (webview:McWebView, url: Uri) -> Boolean)