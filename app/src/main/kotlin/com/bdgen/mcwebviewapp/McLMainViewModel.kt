package com.bdgen.mcwebviewapp

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdgen.mcwebview.composable.McWebviewDownload
import com.bdgen.mcwebview.composable.McWebviewReceivedError
import com.bdgen.mcwebview.core.McScheme
import com.bdgen.mcwebview.core.McWebView
import com.bdgen.mcwebview.plugin.McCommonPlugin
import com.bdgen.mcwebview.plugin.McCommonPlugin.McCommonListener
import com.bdgen.mcwebviewapp.common.McLViewModel

class McLMainViewModel : McLViewModel<McLMainActivity, McLMainViewModel.McLMainViewModelListener> {
    data class McLMainViewModelListener(
        override val activity: () -> McLMainActivity,
        val commonListener: McCommonListener,
        val sendTo: (url: Uri) -> Unit,
        val download: McWebviewDownload,
        val receivedError: McWebviewReceivedError
    ): McLViewModelListener<McLMainActivity>()

    override var listener: McLMainViewModelListener? = null

    lateinit var url :String
    lateinit var commonPlugin :McCommonPlugin

    val mailTo: McScheme = McScheme("mailto") { webview, url ->
        this.listener?.sendTo(url)
        return@McScheme true
    }


    @SuppressLint("StaticFieldLeak")
    var webView: McWebView? = null

    constructor() {}

    constructor(listener: McLMainViewModelListener) {
        this.listener = listener
        this.commonPlugin = McCommonPlugin(listener.commonListener)
    }

    override fun onLaunched(activity: McLMainActivity) {
        this.webView?.addPlugin(this.commonPlugin)
        this.webView?.addScheme(this.mailTo)
    }

    override fun onCleared() {
        Log.d(this.javaClass.simpleName, "WebView 리소스를 해제.")
        this.webView?.destroy()
        this.webView = null
        super.onCleared()
    }

    fun loadWebview() {
        this.webView?.loadUrl(url)
    }

//    val download: McWebviewDownload = { url, userAgent, contentDisposition, mimeType, contentLength ->
//        this.listener.download(url, userAgent, contentDisposition, mimeType, contentLength)
//    }
//
//    val receivedError: McWebviewReceivedError = { errorCode, description, failingUrl ->
//
//    }
}

class McLMainViewModelFactory(private val listener: McLMainViewModel.McLMainViewModelListener) : ViewModelProvider.Factory
{
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(McLMainViewModel::class.java)) {
            return McLMainViewModel(listener) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}