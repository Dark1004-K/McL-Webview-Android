package com.bdgen.mcwebview.result

import com.bdgen.mcwebview.core.McWebParam


class McWebPair(val key: String, val value: String?) : McWebParam() {
    override fun toHashMap(): HashMap<String, *> {
        val result: HashMap<String, String?> = HashMap()
        result.put("key",key)
        result.put("value",value)
        return result
    }
}

