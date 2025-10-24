package com.bdgen.mcwebview.core

import android.app.Dialog
import android.net.Uri
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JsPromptResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView

class McWebChromeClient() : WebChromeClient() {

    interface ShowFileChooserListener {
        fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>?, fileChooserParams: FileChooserParams?): Boolean
    }

    private var showFileChooserListener: ShowFileChooserListener? = null
//    private var dialog: Dialog? = null
//    private var newWebView: McWebView? = null

    fun setOnShowFileChooserListener(listener: ShowFileChooserListener?) {
        showFileChooserListener = listener
    }

    /**
     * windows open !!!
     *
     *     override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: Message): Boolean
     *     {
     *         if (dialog != null) return false
     *         val parentWebView: McWebView? = view as McWebView?
     *         newWebView = McWebView(view.context)
     *         newWebView.setParentWebview(parentWebView)
     *         newWebView.loadDefaultConfigNoClear(parentWebView.getContainerView())
     *
     *         dialog = Dialog(view.getContext())
     *         dialog.setContentView(newWebView)
     *         val height: Int = parentWebView.getHeight()
     *         dialog!!.getWindow()!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, height)
     *         val param = dialog!!.getWindow()!!.getAttributes()
     *         param.gravity = Gravity.BOTTOM
     *         dialog!!.getWindow()!!.setFlags(
     *             WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
     *             WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
     *         ) // Dialog 밖의 View를 터치 가능
     *         dialog!!.getWindow()!!.setAttributes(param)
     *         dialog!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
     *         dialog!!.getWindow()!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
     *         dialog!!.show()
     *         Log.d("DarkAngel", "Window.open 했음")
     *
     *         dialog!!.setOnKeyListener(DialogInterface.OnKeyListener { dialog1: DialogInterface?, keyCode: Int, event: KeyEvent? ->
     *             if (keyCode == KeyEvent.KEYCODE_BACK) {
     *                 if (newWebView.canGoBack()) {
     *                     newWebView.goBack()
     *                 } else {
     *                     Log.d("DarkAngel", "Window.open 종료")
     *                     dialog1!!.dismiss()
     *                     dialog = null
     *                     newWebView.destroy()
     *                     //                    dialog.getOwnerActivity().onBackPressed();
     *                     val context: Context? = newWebView.getParetnWebview().getContext()
     *                     if (context is Activity) {
     *                         context.onBackPressed()
     *                     }
     *                 }
     *                 return@setOnKeyListener true
     *             } else {
     *                 return@setOnKeyListener false
     *             }
     *         })
     *
     *         val transport = resultMsg.obj as WebView.WebViewTransport
     *         transport.setWebView(newWebView)
     *         resultMsg.sendToTarget()
     *
     *         return true
     *     }
     *
     *     fun dialogClose() {
     *         if (dialog != null && newWebView != null) {
     *             onCloseChild()
     *             newWebView.dialogClose()
     *         }
     *     }
     *
     *     fun onCloseChild() {
     *         if (dialog != null) {
     *             newWebView.destroy()
     *             dialog!!.dismiss()
     *             dialog = null
     *         }
     *     }
     *
     *     override fun onCloseWindow(window: WebView) {
     *         val parent: MwWebView? = (window as MwWebView).getParetnWebview()
     *
     *         if (parent != null) {
     *             parent.onCloseChild()
     *         }
     *
     *         super.onCloseWindow(window)
     *     }
     **/

    override fun onShowFileChooser(webView: WebView?, filePathCallback: ValueCallback<Array<Uri?>?>?, fileChooserParams: FileChooserParams?): Boolean
    {
        if (this.showFileChooserListener != null) {
            return this.showFileChooserListener!!.onShowFileChooser(
                webView,
                filePathCallback,
                fileChooserParams
            )
        }
        return false
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        Log.d("McWebview", "[" + consoleMessage.lineNumber() + " : " + consoleMessage.sourceId() + "]")
        Log.d("McWebview", consoleMessage.message())
        Log.d("McWebview", "============================")
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onJsPrompt(view: WebView?, url: String?, message: String, defaultValue: String?, result: JsPromptResult?): Boolean
    {
        Log.d("MwWebview", "[$url]")
        Log.d("MwWebview", message)
        Log.d("MwWebview", "============================")
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }


    /**
     *  AlertDialog !!!
     *
     *     override fun onJsAlert(view: WebView?, url: String?, message: String, result: JsResult): Boolean
     *     {
     *         Log.d("MwWebview", "[" + url + "]")
     *         Log.d("MwWebview", message)
     *         Log.d("MwWebview", "============================")
     *         val webView: MwWebView = view as MwWebView
     *         McPopup.Builder(webView.getContext())
     *             .setContent(message)
     *             .setRightBtn("확인", { dialog, which ->
     *                 result.confirm()
     *                 dialog.dismiss()
     *             })
     *             .show()
     *         return true
     * //        return super.onJsAlert(view, url, message, result);
     *     }
     **/
}