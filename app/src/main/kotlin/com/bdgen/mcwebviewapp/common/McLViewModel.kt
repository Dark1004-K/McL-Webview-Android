package com.bdgen.mcwebviewapp.common

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel

abstract class McLViewModel<A : McLComponentActivity<*, *, *>, L: McLViewModel.McLViewModelListener<A>> : ViewModel() {

    abstract class McLViewModelListener<A : McLComponentActivity<*, *, *>> {
        abstract val activity: () -> A
    }

    val activity get() = listener?.activity()
    abstract var listener : L?
    @CallSuper
    abstract fun onLaunched(activity: A)
}