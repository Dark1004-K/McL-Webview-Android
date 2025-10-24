package com.bdgen.mcwebview.core
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import com.bdgen.mcwebview.result.McWebFailure
import com.google.gson.Gson

abstract class McWebPlugin() {
    lateinit var webView: McWebView
//    abstract val scriptKeyword: String;// = "McPlugin";
    protected var functions = HashMap<String, McWebFunction>()
    abstract var name: String
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        functions = McFunctionRegistrar.registerFunctions(this)
    }

    fun removeAll() {
        functions.clear()
    }

    fun sendResult(request: McWebRequest) {
        sendResult(request.getStatus(), request.getCallbackId(), request.getParam())
    }

    fun sendResult(status: McResponseStatus, callbackId: String?, param: McWebParam?) {
            val callFunc: String? = status.value
            val empty = Base64.encodeToString("{}".toByteArray(), Base64.NO_WRAP)

            if (param == null) {
                webView.evaluateJavascript("${name}Result.$callFunc($callbackId, \"$empty\")", null)
                return
            }

            val gson = Gson()
            val json: String? = gson.toJson(param.toHashMap())
            if (json == null || json.isEmpty()) {
                webView.evaluateJavascript("${name}Result.failure($callbackId, \"$empty\")", null)
                return
            }
            val base64 = Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
            if (base64 == null || base64.isEmpty()) {
                webView.evaluateJavascript("${name}Result.failure($callbackId, \"$empty\")", null)
                return
            }

            webView.evaluateJavascript("${name}Result.$callFunc($callbackId, \"$base64\")", null)
    }

    fun messageHandlers(message: String?) {
        if (message == null) return
        val callbackId: String?
        val functionName: String?
        val gson = Gson()
        val map: HashMap<String?, *> = gson.fromJson<HashMap<String?, *>>(message, HashMap::class.java)

        functionName = map["funcName"] as String?
        callbackId = map["callbackId"] as String?

        if(functionName.isNullOrEmpty()|| callbackId.isNullOrEmpty()) {
            sendResult(McResponseStatus.failure, "-1", McWebFailure("콜백 아이디 오류"))
            return
        }

        val paramJson = String(Base64.decode(map["param"] as String?, Base64.DEFAULT))
        val param: HashMap<String, *> = gson.fromJson<HashMap<String, *>>(paramJson, HashMap::class.java)
        this.callScript(functionName, param, callbackId)
    }

    private fun callScript(funcName: String, param: HashMap<String, *>, callbackId: String) {
        val function: McWebFunction? = functions[funcName]
        if (function == null) {
            sendResult(McResponseStatus.failure, callbackId, McWebFailure("$funcName 함수를 찾을 수 없습니다."))
            return
        }
        try {
            function.exec(callbackId, param)
        } catch (cx: ClassCastException) {
            sendResult(McResponseStatus.failure, callbackId, McWebFailure(funcName + "잘못된 변수 타입입니다."))
        }
    }

    @JavascriptInterface
    fun postMessage(message: String?) {
        Handler(Looper.getMainLooper()).post {
            Log.d("sumer", "[post message]  message >>> $message")
            messageHandlers( message)
        }
    }

    //    fun getter(info: MwStorageModel?, callbackId: String?) {
//        if (info == null) {
//            sendResult(
//                MwPlugInRequest.ResponseType.failure,
//                callbackId,
//                MwFailureResultModel("요청 정보가 없습니다.")
//            )
//            return
//        }
//        this.sendResult(
//            MwPlugInRequest.ResponseType.success,
//            callbackId,
//            MwStorageResultModel(info)
//        )
//    }
//
//    //요기부터!!!!!!!!!! - 24.03.08 By.sumer
//    fun setter(info: MwStorageModel?, param: HashMap<String?, *>?, callbackId: String?) {
//        if (info == null) {
//            sendResult(
//                MwPlugInRequest.ResponseType.failure,
//                callbackId,
//                MwFailureResultModel("정보를 저장할 수 없습니다.")
//            )
//            return
//        }
//
//        // json -> hash -> json ?? 이게 맞나 싶다... 그런데 법칙을 따르자면... 훌훌훌...
//        val gson: com.google.gson.Gson = com.google.gson.Gson()
//        val json: String? = gson.toJson(param)
//        info.setInfo(json)
//        this.sendResult(MwPlugInRequest.ResponseType.success, callbackId, null)
//    }

}