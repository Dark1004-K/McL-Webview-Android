package com.bdgen.mcwebview.core
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import com.bdgen.mcwebview.BuildConfig



class McWebView : WebView {
    constructor(context: Context): super(context)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, privateBrowsing: Boolean): super(context, attrs, defStyleAttr, privateBrowsing)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes)
    private val agent = ";McWebView;Mcl(os:Android, versionCode:" + BuildConfig.VERSION_CODE + ", versionName:" + BuildConfig.VERSION_NAME + ", osVersion:" + Build.VERSION.RELEASE + ");"
    private var plugins: HashMap<String, McWebPlugin> = HashMap<String, McWebPlugin>()

    fun initialize() {
        this.loadDefaultConfig()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadConfig() {
        webViewClient = McWebViewClient();
        webChromeClient = McWebChromeClient();

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
        plugins.put(plugin.name, plugin)
        try {
            addJavascriptInterface(plugin, plugin.name)
        } catch (e: Exception) {
            Log.d("sumer", "addJavascriptInterface error")
        }
    }
}