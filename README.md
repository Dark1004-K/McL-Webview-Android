# 🌐 McL-Webview-Android

[![JitPack](https://jitpack.io/v/Dark1004-K/McL-Webview-Android.svg)](https://jitpack.io/#Dark1004-K/McL-Webview-Android)
[![GitHub license](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)

**McL-Webview-Android**는 Android의 기본 `WebView` 기능을 확장하고 필수 설정을 자동화하여, 하이브리드 앱 개발을 간소화하는 커스텀 `WebView` 컴포넌트입니다. 복잡한 설정 코드 없이 빠르고 안정적인 웹뷰 환경을 구축할 수 있습니다.

## ✨ 주요 특징 (Features)

* **자동 초기 설정**: JavaScript, DOM Storage, Wide Viewport 등 웹뷰 사용에 필요한 기본적인 `WebSettings`를 자동으로 활성화하여 설정 단계를 최소화합니다.
* **간편한 통합**: XML 레이아웃에 `McWebview`만 추가하면 바로 사용할 수 있습니다.
* **뒤로 가기 처리**: 액티비티의 `onBackPressed()`에서 웹뷰 내 히스토리 이동을 쉽게 구현할 수 있도록 지원합니다.


## 📦 설치 (Installation)
이 라이브러리는 **JitPack**을 통해 배포됩니다.

### 1. JitPack 저장소 추가
프로젝트의 루트 레벨 `settings.gradle` 파일에 JitPack Maven 저장소를 추가합니다.

```groovy
// settings.gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // JitPack 저장소 추가
        maven { url '[https://jitpack.io](https://jitpack.io)' }
    }
}
```
### 2. 의존성 추가

모듈 레벨의 `build.gradle` 파일의 `dependencies` 블록에 라이브러리 의존성을 추가합니다.

🚨 **주의:** `<Tag>` 부분은 이 저장소의 최신 **GitHub Release 태그**로 반드시 대체해야 합니다.

```groovy
// build.gradle (Module: app)
dependencies {
    // McL-Webview-Android 의존성 추가
    implementation 'com.github.Dark1004-K:McL-Webview-Android:<Tag>' 

    // 예시: (최신 버전이 v1.0.0일 경우)
    // implementation 'com.github.Dark1004-K:McL-Webview-Android:v1.0.0'
}
```


## ⚙️ 설정 (Configuration)

### 1. Android Manifest 권한 추가
웹 콘텐츠를 로드하기 위해 AndroidManifest.xml에 인터넷 사용 권한이 필요합니다.
```AndroidManifest.xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.your.app">
    
    <uses-permission android:name="android.permission.INTERNET" />
    
    <application
        ...
        >
        ...
    </application>
</manifest>
```


## 🚀 사용법 (Usage Guide)

### 1. XML 레이아웃에 추가
레이아웃 파일에 커스텀 McWebview를 사용합니다.
```XML
<RelativeLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.bdgen.mcwebview.McWebview
        android:id="@+id/mc_webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</RelativeLayout>
```

### 2. Kotlin 코드에서 사용 및 URL 로드
액티비티나 프래그먼트에서 McWebview를 초기화하고 사용합니다.
```kotlin
import com.dark1004.mcwebview.McWebview
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    private lateinit var mcWebview: McWebview

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mcWebview = findViewById(R.id.mc_webview)

        // 웹뷰를 사용하여 URL 로드
        mcWebview.loadUrl("https://www.google.com")
        
        // 추가: 웹뷰 클라이언트 커스터마이징이 필요한 경우
        // mcWebview.webViewClient = CustomWebViewClient()
        // mcWebview.webChromeClient = CustomWebChromeClient()
    }

    // 💡 필수: 뒤로 가기 버튼 처리 (웹뷰 내에서 이전 페이지로 이동)
    override fun onBackPressed() {
        if (mcWebview.canGoBack()) {
            mcWebview.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
```


## 📚 McWebview의 자동 설정 목록
McWebview는 표준 웹뷰의 초기화 시 다음의 핵심 설정을 자동으로 적용합니다.

| 설정 (WebSettings)       | 값 (Value) | 목적                                                         |
| ------------------------| --------- | ----------------------------------------------------------- | 
| javaScriptEnabled       | true      | 웹 페이지 내 JavaScript 실행을 허용합니다.                       | 
| domStorageEnabled       | true      | 로컬 저장소 (DOM Storage) 사용을 허용하여 HTML5 Web Storage를 지원합니다. |
| loadWithOverviewMode     | true     | 웹 콘텐츠가 웹뷰의 가로 크기에 맞춰지도록 합니다.                |
| useWideViewPort          | true     | 뷰포트 <meta> 태그를 지원하여 콘텐츠가 화면 크기에 맞게 표시되도록 합니다. |
| supportZoom              | false    | 사용자의 줌 확대/축소 기능을 비활성화합니다.                     |


## 📄 라이센스 (License)
이 프로젝트는 Apache License 2.0을 따릅니다. 자세한 내용은 LICENSE 파일을 참조하세요.
```
Copyright 2025 Dark1004-K

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
```
