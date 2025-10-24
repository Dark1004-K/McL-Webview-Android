package com.bdgen.mcwebview.composable

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.bdgen.mcwebview.core.McWebPlugin
import com.bdgen.mcwebview.core.McWebView

@Composable
fun McWebView(modifier: Modifier = Modifier, onCreated:((McWebView) -> Unit)? = null)//, plugins:List<McWebPlugin>? = null)
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
                onCreated?.invoke(this)
//                for (plugin in plugins ?: return@Init) addPlugin(plugin)
            }
        }
    )
}
//class McWebViewController(var webViewcom.bd.mamfdev ----------------------------
//    fun loadUrl(url: String) {
//        Log.d("${this.javaClass.name}", "setView")
//        webViewInstance?.loadUrl(url)
//    }
//
//    fun evaluateJavascript(script:String, resultCallback: ValueCallback<String>?) {
//        webViewInstance?.evaluateJavascript(script, resultCallback)
//    }
//
//    fun addPlugin(plugin: McWebPlugin) {
//        webViewInstance?.addPlugin(plugin)
//    }
//}