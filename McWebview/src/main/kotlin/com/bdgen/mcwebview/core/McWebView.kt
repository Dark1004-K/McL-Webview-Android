package com.bdgen.mcwebview.core
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import com.bdgen.mcwebview.BuildConfig


class McWebView : WebView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)

//    @Deprecated("Deprecated")
//    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, privateBrowsing: Boolean): super(context, attrs, defStyleAttr, privateBrowsing)

    private val agent = ";McWebView;Mcl(os:Android, versionCode:" + BuildConfig.VERSION_CODE + ", versionName:" + BuildConfig.VERSION_NAME + ", osVersion:" + Build.VERSION.RELEASE + ");"
    private var plugins: HashMap<String, McWebPlugin> = HashMap<String, McWebPlugin>()
    var schemes: HashMap<String, McScheme> = HashMap<String, McScheme>()

    lateinit var mcWebViewClient: McWebViewClient
    lateinit var mcWebChromeClient: McWebChromeClient


    fun initialize() {
        mcWebViewClient = McWebViewClient()
        mcWebChromeClient = McWebChromeClient()

        webViewClient = this.mcWebViewClient
        webChromeClient = this.mcWebChromeClient
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadConfig() {

        val settings = getSettings()
        val newAgent = settings.userAgentString + agent
        settings.setUserAgentString(newAgent)
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.javaScriptCanOpenWindowsAutomatically = true

        settings.setSupportMultipleWindows(true)
        settings.domStorageEnabled = true
        settings.loadWithOverviewMode = true
        settings.defaultTextEncodingName = "UTF-8"

        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.loadsImagesAutomatically = true
        settings.useWideViewPort = true
        settings.setSupportZoom(false)

        settings.allowFileAccess = true
        settings.allowContentAccess = true
//        settings.allowFileAccessFromFileURLs = true
//        settings.allowUniversalAccessFromFileURLs = true

        settings.minimumFontSize = 1
        settings.minimumLogicalFontSize = 1
        settings.textZoom = 100

        settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
    }

    fun loadDefaultConfig() {

        Log.d("sumer", "loadDefaultConfig")
        this.loadConfig()
        this.clearCache(true)
        // 쿠키 지울 경우 로직 만들것!!
        if (BuildConfig.DEBUG) {
            setWebContentsDebuggingEnabled(true)
        }
    }

    fun addPlugin(plugin: McWebPlugin) {
        Log.d("sumer", "addPlugin : ${plugin.name} addr: $plugin")
        plugin.webView = this
        this.plugins[plugin.name] = plugin
        try {
            addJavascriptInterface(plugin, plugin.name)
        } catch (e: Exception) {
            Log.d("sumer", "addJavascriptInterface error")
        }
    }

    fun addScheme(scheme: McScheme) {
        Log.d("sumer", "addScheme : ${scheme.name} addr: $scheme")
//        plugin.webView = this
        this.schemes[scheme.name] = scheme
    }
}