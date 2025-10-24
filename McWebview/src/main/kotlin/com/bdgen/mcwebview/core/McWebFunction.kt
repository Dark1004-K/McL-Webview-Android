package com.bdgen.mcwebview.core

open class McWebFunction(val name: String, val function: (callbackId: String, param: HashMap<String, *>?) -> Unit)
{
    fun exec(callbackId: String, param: HashMap<String, *>?) {
        return function(callbackId, param)
    }
 }

