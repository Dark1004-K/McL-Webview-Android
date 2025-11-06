package com.bdgen.mcwebviewapp.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview

abstract class McLView<VM: McLViewModel<*, *>> {
    protected var model: VM?

    protected constructor() { model = null; }
    constructor(model: VM) { this.model = model }

    @Composable
    abstract fun Content(model: VM)


    @Composable
    fun LoadContent(model: VM) {
        @Suppress("UNCHECKED_CAST")
        Content(model)
    }

    @Preview(showBackground = true)
    @Composable
    abstract fun Preview()

}