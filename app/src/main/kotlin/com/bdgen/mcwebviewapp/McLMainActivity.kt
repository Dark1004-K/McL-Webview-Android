package com.bdgen.mcwebviewapp

import android.app.DownloadManager
import android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
import android.content.Intent
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.webkit.URLUtil
import android.widget.Toast
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
        ),
        sendTo = { url ->
            val intent = Intent(Intent.ACTION_SENDTO, url)
            startActivity(intent)
        },
        download = { url, userAgent, contentDisposition, mimeType, contentLength ->
            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(url.toUri())
            val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)

            // 4. 다운로드 요청 상세 설정
            request.setMimeType(mimeType)
            // 다른 앱에서도 다운로드 파일을 볼 수 있도록 허용

            request.setAllowedOverMetered(true) // 데이터 네트워크에서도 다운로드 허용
//            request.setAllowedOverRoaming(true) // 로밍 중에도 다운로드 허용
            request.setNotificationVisibility(VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // 다운로드 중 및 완료 시 알림 표시
            request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, fileName)


            downloadManager.enqueue(request)
            Toast.makeText(this, "다운로드를 시작합니다...", Toast.LENGTH_SHORT).show()
        },
        receivedError = { view, request, error ->
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return@McLMainViewModelListener true
        }
    )

    private fun startActionView(url : String) {
        Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            this@McLMainActivity.startActivity(this)
        }
    }
}