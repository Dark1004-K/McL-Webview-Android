package com.bdgen.mcwebview.core

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.http.SslError
import android.util.Log
import android.webkit.CookieManager
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import java.net.URISyntaxException

class McWebViewClient() : WebViewClient() {
//    private val context: Context? = null
    private var savedCookie: String? = null
    /**
     * SSL 오류 일 경우 처리 할 것!!!
     *     Alert 처리도 같이 고민해야함.. By DarkAngel
     *
     *     android.app.AlertDialog.Builder(this.context)
     *     .setContent("이 사이트의 보안 인증서는 신뢰하는 보안 인증서가 아닙니다. 계속하시겠습니까?")
     *     .setRightBtn("계속하기",
     *     { dialogInterface, i -> handler.proceed() })
     *     .setLeftBtn("취소",
     *     { dialog, i -> handler.cancel() dialog.dismiss() })
     *     .show()
     */
    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
    }

/**
 * 네트워크 감지 에러!!
 * 나중에 고민 할 것.
 */
    //    @Override
    //    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
    //        super.onReceivedError(view, request, error);
    //        MwWebView webView = (MwWebView) view;
    //        Log.d("sumer","[onReceivedError] >>>>>>>>>>> error"+error.getErrorCode()); */
    //        webView.networkAlert();
    //    }
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return onloadUrl(view, request.url.toString())
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return onloadUrl(view, url)
    }


    /**
     * 다른 App(외부 앱 ex- 카드사..) 호출 시 현대카드 url이 다르게 전달받고 있어 분기처리 로직 수정 - by.sumer
     * 기존 - intent://
     * 변경 - intent:
     *
     * ex) 롯데카드 :  intent://lottecard/data?acctid=202407091302327456233064926514&apptid=657387&inappPay=N&bldc=N#Intent;scheme=lotteappcard;package=com.lcacApp;end
     * 현대카드 :  intent:hdcardappcardansimclick://appcard?acctid=202407091301136293639658454881#Intent;package=com.hyundaicard.appcard;end;
     *
     * 참고 url : https://m.blog.naver.com/overhere-kr/221115439264 (17년도부터 현대카드 스키마 오류 있었음)
     */
    private fun onloadUrl(view: WebView, url: String) : Boolean {
        val cookieManager = CookieManager.getInstance()
        val cookie = cookieManager.getCookie(url)
        if (cookie != null) {
            savedCookie = cookie
        } else {
            if (savedCookie != null) cookieManager.setCookie(url, savedCookie)
        }

        //웹뷰 내 표준창에서 외부앱(통신사 인증앱)을 호출하려면 intent:// URI를 별도로 처리해줘야 합니다.
        //다음 소스를 적용 해주세요.
        Log.d("McWebView", "url : " + url)

        if (url.startsWith("intent:")) {
            var intent: Intent? = null
            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                if (intent != null) {
                    //앱실행
                    view.context.startActivity(intent)
                }
            } catch (e: URISyntaxException) {
                //URI 문법 오류 시 처리 구간
            } catch (e: ActivityNotFoundException) {
                val packageName = intent!!.getPackage()
                if (packageName != "") {
                    // 앱이 설치되어 있지 않을 경우 구글마켓 이동
                    view.context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            ("market://details?id=$packageName").toUri()
                        )
                    )
                }
            }
            //return 값을 반드시 true로 해야 합니다.
            return true
        } else if (url.startsWith("https://play.google.com/store/apps/details?id=") || url.startsWith(
                "market://details?id="
            )
        ) {
            //표준창 내 앱설치하기 버튼 클릭 시 PlayStore 앱으로 연결하기 위한 로직
            val uri = url.toUri()
            val packageName = uri.getQueryParameter("id")
            if (packageName != null && packageName != "") {
                // 구글마켓 이동
                view.context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        ("market://details?id=$packageName").toUri()
                    )
                )
            }
            //return 값을 반드시 true로 해야 합니다.
            return true
        }
        return false
    }

}