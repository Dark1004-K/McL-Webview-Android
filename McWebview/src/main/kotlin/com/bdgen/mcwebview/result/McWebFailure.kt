package com.bdgen.mcwebview.result

import com.bdgen.mcwebview.core.McWebParam

class McWebFailure(val message: String?) : McWebParam() {
    override fun toHashMap(): HashMap<String, *> {
        val result: HashMap<String, String?> = HashMap()
        result.put("message", this.message)
        return result
    }
}