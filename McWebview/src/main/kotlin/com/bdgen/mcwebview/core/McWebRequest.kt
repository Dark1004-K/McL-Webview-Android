package com.bdgen.mcwebview.core

import java.io.Serializable

enum class McResponseStatus(value: String) {
    success("success"),
    failure("failure");

    var value: String?

    init {
        this.value = value
    }
}

class McWebRequest() : Serializable {
    private lateinit var type: McResponseStatus
    private lateinit var callbackId: String
    private var param: McWebParam? = null

    constructor(type: McResponseStatus, callbackId: String, param: McWebParam?) : this() {
        this.type = type
        this.callbackId = callbackId
        this.param = param
    }
    constructor(callbackId: String) : this() {
        this.type = McResponseStatus.failure
        this.callbackId = callbackId
        this.param = null
    }


    fun getStatus(): McResponseStatus {
        return type
    }

    fun setStatus(type: McResponseStatus) {
        this.type = type
    }

    fun getCallbackId(): String {
        return callbackId
    }

    fun setCallbackId(callbackId: String) {
        this.callbackId = callbackId
    }

    fun getParam(): McWebParam? {
        return param
    }

    fun setParam(param: McWebParam?) {
        this.param = param
    }



}