package com.bdgen.mcwebview.plugin

import android.content.Context
import android.util.Log
import com.bdgen.mcwebview.core.McResponseStatus
import com.bdgen.mcwebview.core.McWebMethod
import com.bdgen.mcwebview.core.McWebPlugin
import com.bdgen.mcwebview.result.McWebPair
import com.bdgen.mcwebview.util.McPreference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class McCommonPlugin(val listener: McCommonListener) : McWebPlugin() {
    override var name: String  = "McCommonPlugin"
    data class McCommonListener (
        val onClose: () -> Unit,
        val onLoadBrowser: (url: String) -> Unit,
        val getContext: () -> Context
    )

    @McWebMethod
    fun closeApp(callbackId: String) { // I/F 네이밍 변경
        Log.d(this.name, "onClose")
        listener.onClose()
        sendResult(McResponseStatus.success, callbackId, null)
    }

    @McWebMethod
    fun loadUrl(callbackId: String, url: String) {
        Log.d(this.name, "loadUrl  >> $url")
        webView.loadUrl(url)
        sendResult(McResponseStatus.success, callbackId, null)
    }

    @McWebMethod
    fun loadUrlForBrowser(callbackId: String, url: String) {
        Log.d(this.name, "loadUrlForBrowser  >> $url")
        listener.onLoadBrowser(url)
        sendResult(McResponseStatus.success, callbackId, null)
    }

    @McWebMethod
    fun getProperty(callbackId: String, key:String) {
        McPreference.init(listener.getContext())
        Log.d(this.name, "getProperty ")
        var value = ""
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                value = McPreference.read(listener.getContext(), key)
            }
            sendResult(McResponseStatus.success, callbackId, McWebPair(key,value))
        }
    }

    @McWebMethod
    fun setProperty(callbackId: String, key:String, value:String) {
        Log.d(this.name, "setProperty ")
        McPreference.init(listener.getContext())
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.IO) {
                McPreference.write(listener.getContext(), key, value)
            }
            sendResult(McResponseStatus.success, callbackId, null)
        }
    }

    fun onBackPressed() {
        webView.evaluateJavascript(this.name + ".onBackPressed()", null)
    }

    /**
     * TODO:
     * - callTokTokApp I/F
     * - loadUrlForWebView I/F
     * - 강업 I/F
     */

//    Ojbect Param Sample 임!! 헤메지 말자!!
//    @McWebMethod
//    fun receiveObj(callbackId: String, obj: LinkedTreeMap<String, *>) {
//        Log.d(this.name, "receiveObj  >> ${obj.toString()}")
//        sendResult(McResponseStatus.failure, callbackId, null)
//    }

//    @McWebMethod
//    fun receive(callbackId: String, ok: Boolean) {
//        Log.d(this.name, "receive  >> $ok")
////        webView.loadUrl(url)
//        sendResult(McResponseStatus.success, callbackId, null)
//    }
}