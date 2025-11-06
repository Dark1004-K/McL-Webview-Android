package com.bdgen.mcwebviewapp.common

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.CallSuper
import androidx.compose.runtime.LaunchedEffect


abstract class McLComponentActivity<A : McLComponentActivity<A, V, VM>, V : McLView<VM>, VM : McLViewModel<A, *>>: ComponentActivity()
{
    protected abstract val model: VM
    protected lateinit var view: V private set

    @CallSuper
    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.view = createView()

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE); //캡쳐 방지
        setContent {
            view.LoadContent(model)
            LaunchedEffect(Unit) {
                model.onLaunched(this@McLComponentActivity as A)
                this@McLComponentActivity.onLaunched()
            }
        }
    }

    abstract val createView: () -> V

    abstract suspend fun onLaunched()

}