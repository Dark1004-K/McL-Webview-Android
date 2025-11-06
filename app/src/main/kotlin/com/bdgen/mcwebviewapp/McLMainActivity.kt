package com.bdgen.mcwebviewapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.bdgen.mcwebview.plugin.McCommonPlugin
import com.bdgen.mcwebviewapp.common.McLComponentActivity

class McLMainActivity() : McLComponentActivity<McLMainActivity, McLMainView, McLMainViewModel>() {
    override val model: McLMainViewModel by viewModels {
        McLMainViewModelFactory(viewModelListener)
    }

    override val createView = { McLMainView(model) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model.url = "http://192.168.0.215:3000"
//        model.url = "https://www.naver.com"
    }

    override suspend fun onLaunched() {
        model.loadWebview()
    }

    val viewModelListener = McLMainViewModel.McLMainViewModelListener(
        activity = { return@McLMainViewModelListener this },
        commonListener = McCommonPlugin.McCommonListener(
            onClose = {
                moveTaskToBack(true); // 백그라운드 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
            },
            onLoadBrowser = { url -> startActionView(url) },
            getContext = { return@McCommonListener this }
        )
    )

    private fun startActionView(url : String) {
        Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            this@McLMainActivity.startActivity(this)
        }
    }
}