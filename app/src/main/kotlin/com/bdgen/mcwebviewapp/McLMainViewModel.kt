package com.bdgen.mcwebviewapp

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bdgen.mcwebview.core.McWebPlugin
import com.bdgen.mcwebview.core.McWebView
import com.bdgen.mcwebview.plugin.McCommonPlugin
import com.bdgen.mcwebview.plugin.McCommonPlugin.McCommonListener
import com.bdgen.mcwebviewapp.common.McLViewModel

class McLMainViewModel : McLViewModel<McLMainActivity, McLMainViewModel.McLMainViewModelListener> {
    data class McLMainViewModelListener(
        override val activity: () -> McLMainActivity,
        val commonListener: McCommonListener
    ): McLViewModelListener<McLMainActivity>(activity)

    override lateinit var listener: McLMainViewModelListener

    lateinit var url :String
    lateinit var commonPlugin :McCommonPlugin


    @SuppressLint("StaticFieldLeak")
    var webView: McWebView? = null

    constructor() {}

    constructor(listener: McLMainViewModelListener) {
        this.listener = listener
        this.commonPlugin = McCommonPlugin(listener.commonListener)
    }

    override fun onLaunched(activity: McLMainActivity) {
        for (plugin in this.getPlugins()) webView?.addPlugin(plugin)
    }

    override fun onCleared() {
        Log.d(this.javaClass.simpleName, "WebView 리소스를 해제.")
        this.webView?.destroy()
        this.webView = null
        super.onCleared()
    }

    fun getPlugins() : List<McWebPlugin> {
        return listOf(commonPlugin)
    }

    fun loadWebview() {
        webView?.loadUrl(url)
    }
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