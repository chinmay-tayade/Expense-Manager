import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    kotlin("kapt")
}
val keystoreProperties = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")

if (keystorePropertiesFile.exists()) {
    FileInputStream(keystorePropertiesFile).use { keystoreProperties.load(it) }
}

android {
    namespace = "com.chinmay.expensetracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.chinmay.expensetracker"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            val appName = "Bachatt"
            val versionName = versionName
            val versionCode = versionCode
            val buildType = buildType.name
            val flavor = if (flavorName.isNotEmpty()) "$flavorName-" else ""
            val dateFormat = SimpleDateFormat("dd-MM_HH-mm")
            val buildTime = dateFormat.format(Date())

            outputImpl.outputFileName = "${appName}-${flavor}${buildType}-v${versionName}(${versionCode})-$buildTime.apk"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.ui)
    implementation(libs.ui.tooling)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.android.v244)
    kapt(libs.hilt.android.compiler.v244)
    implementation(libs.androidx.viewfinder.core)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.ui.test.android)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.kotlin.stdlib)
    ksp(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)
    implementation(libs.androidx.media3.exoplayer.hls)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    implementation(libs.foundation)
    androidTestImplementation(libs.androidx.foundation)
    implementation(libs.lottie)
    implementation(libs.lottie.compose)
    implementation(libs.mpandroidchart)
    implementation(libs.exoplayer)
    implementation(libs.coil.compose)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.auth.api.phone)
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.greenrobot.eventbus)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.analytics)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.mixpanel.android)
    debugImplementation(libs.library)
    releaseImplementation(libs.library.no.op)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.rxjava3)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.appsflyer.af.android.sdk)
    implementation(libs.installreferrer)
    testImplementation(libs.androidx.room.testing)
    implementation(libs.firebase.crashlytics)
    implementation(libs.com.google.firebase.firebase.analytics)
    implementation(libs.checkout)
    implementation(libs.signature.pad)
    implementation(libs.play.services.location)
    implementation(libs.androidx.webkit)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.firebase.config.ktx)
    implementation(libs.accompanist.swiperefresh)
    implementation(libs.timber)
    implementation(libs.androidx.lifecycle.process)
    implementation(libs.firebase.messaging)
    implementation(libs.app.update)
    implementation(libs.app.update.ktx)
    implementation (libs.clevertap.android.sdk)
    implementation(libs.review)
    implementation(libs.review.ktx)
    implementation(libs.accompanist.systemuicontroller.v0301)
    implementation (libs.accompanist.permissions)
    implementation(libs.glide)

    // WorkManager
    implementation (libs.androidx.work.runtime.ktx)
    androidTestImplementation (libs.androidx.work.testing)

    // Glide
    implementation(libs.compose.glide)
    implementation(libs.accompanist.placeholder.material)
    // Audio recorder - use a version that supports 16KB alignment or use alternative
    //    implementation(libs.audio.recorder)  // Comment out if causing 16KB issues

    // Alternative: Use Android's built-in MediaRecorder or a 16KB-compatible library
    // implementation("io.github.adrielcafe.voyager:voyager-navigator:1.0.0")  // Example alternative
    implementation(libs.coil.compose.v270)
    implementation(libs.coil.gif.v270)
    implementation(libs.androidx.sqlite.ktx)

    // HLS support (for .m3u8)
    implementation(libs.androidx.media3.exoplayer.hls)

    // Caching support
    implementation(libs.media3.datasource.okhttp)

    // Downloader APIs
    implementation(libs.media3.exoplayer.dash)
    implementation(libs.media3.exoplayer.hls)

    // Database for caching
    implementation(libs.media3.database)

    implementation (libs.facebook.android.sdk.v1620)
    implementation(libs.facebook.core)

    implementation(libs.coil.svg)
    implementation(libs.coil.compose.v250)
    implementation(libs.coil.gif.v250)

}