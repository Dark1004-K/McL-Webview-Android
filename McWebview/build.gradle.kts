import java.io.ByteArrayOutputStream
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.bdgen.mcwebview"
    compileSdk = 36

    buildFeatures {
        buildConfig = true // Enable BuildConfig generation
        compose = true
        viewBinding = true
    }

    defaultConfig {
        minSdk = 24

        val sdkVersionName = "1.0.5"
        val versionCode = 5

        val commitHash = try {
            val stdout = ByteArrayOutputStream()
            exec {
                commandLine("git", "rev-parse", "--verify", "--short", "HEAD")
                standardOutput = stdout
                isIgnoreExitValue = true // 명령 실패 시 예외를 발생시키지 않음
            }
            stdout.close()
            stdout.toString().trim().ifEmpty { "unknown" }
        } catch (e: Exception) {
            println("Warning: Could not get git commit hash. Defaulting to 'unknown'. Error: ${e.message}")
            "unknown" // 예외 발생 시 기본값
        }
        println("build hash git............... : $commitHash")
        setProperty("archivesBaseName", "mcwebview_v${sdkVersionName}_${commitHash}")

        buildConfigField("String", "BUILD_VERSION", "\"${commitHash}\"")
        buildConfigField("String", "VERSION_NAME", "\"${sdkVersionName}\"")
        buildConfigField ("String", "VERSION_CODE", "\"${versionCode}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin {
        jvmToolchain(17)
    }

    buildToolsVersion = "35.0.0"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.tink.android)
    implementation(kotlin("reflect"))
    implementation(libs.gson)
}