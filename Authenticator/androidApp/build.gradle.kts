plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android")
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    // Expandable FAB
    implementation("com.nambimobile.widgets:expandable-fab:1.1.1")
    // Qr Code Implmentations
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    // OTP Generator
    implementation("dev.turingcomplete:kotlin-onetimepassword:2.1.0")
    implementation("commons-codec:commons-codec:1.15")
    // Gson
    implementation("com.google.code.gson:gson:2.8.6")
    // Desugaring (required to use LocalDateTime.now())
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    // Google drive
    implementation("com.google.android.gms:play-services-auth:19.0.0")
    implementation("com.google.api-client:google-api-client-android:1.22.0")
    implementation("com.google.apis:google-api-services-drive:v3-rev75-1.22.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("Z:\\Keystore (apps)\\key.jks")
            storePassword = "Stawrejo9"
            keyAlias = "key"
            keyPassword = "Stawrejo9"
        }
    }
    lintOptions {
        isCheckReleaseBuilds = false
        isAbortOnError = false
    }
    compileSdkVersion(30)
    kotlinOptions {
        jvmTarget = "1.8"
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
    defaultConfig {
        applicationId = "com.tomtruyen.soteria.android"
        minSdkVersion(19)
        targetSdkVersion(30)
        versionCode = 4
        versionName = "1.3"
        multiDexEnabled = true
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}