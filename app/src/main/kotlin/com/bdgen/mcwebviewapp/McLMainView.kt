package com.bdgen.mcwebviewapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.bdgen.mcwebview.composable.McWebView
import com.bdgen.mcwebview.core.McWebView
import com.bdgen.mcwebviewapp.common.McLView
import com.bdgen.mcwebviewapp.ui.theme.McWebviewAppTheme

class McLMainView : McLView<McLMainViewModel> {
    constructor() : super()
    constructor(model: McLMainViewModel) : super(model)

    @Composable
    override fun Content(model: McLMainViewModel) {
        val currentModel = model
        val view = LocalView.current
        McWebviewAppTheme {
            SideEffect {
                if (!view.isInEditMode) {
                    WindowCompat.getInsetsController(model.activity!!.window, view).run {
                        show(WindowInsetsCompat.Type.navigationBars())
                        isAppearanceLightNavigationBars = true
                        isAppearanceLightStatusBars = true
                    }
                }
            }
            McWebviewScreen(currentModel)
        }
    }

    @Composable
    fun McWebviewScreen(model: McLMainViewModel) {
        val isPreview = LocalInspectionMode.current
        Scaffold(modifier = Modifier.fillMaxSize())
        { padding ->
            McWebView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                onCreated = { view ->
                    model.webView = view
                   if(!isPreview) model.webView?.loadDefaultConfig()
                },
                onDownload = model.listener?.download,
                onReceivedError = model.listener?.receivedError,
            )
        }
    }


    @Preview(showBackground = true)
    @Composable
    override fun Preview() {
        val currentModel = McLMainViewModel()
        McWebviewAppTheme {
            McWebviewScreen(currentModel)
        }
    }
}