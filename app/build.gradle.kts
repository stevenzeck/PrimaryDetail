@file:Suppress("UnstableApiUsage")

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.parcelize")
    kotlin("plugin.serialization")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.primarydetail"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isDebuggable = true
        }
    }

    // Need to include this for lambda
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
    }
    namespace = "com.example.primarydetail"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.kotlin.stdlib)

    // AndroidX Core
    implementation(libs.preference)
    implementation(libs.appcompat)
    implementation(libs.androidx.core)
    implementation(libs.constraintlayout)
    implementation(libs.recyclerview)
    implementation(libs.recyclerview.selection)
    implementation(libs.swiperefreshlayout)

    // Fragments/Navigation
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // ViewModel
    implementation(libs.lifecycle.viewmodel)
    @Suppress("LifecycleAnnotationProcessorWithJava8")
    kapt(libs.lifecycle.compiler)

    // Room Database
    implementation(libs.bundles.room)
    implementation(libs.window)
    implementation(libs.startup)
    kapt(libs.room.compiler)

    // Material
    implementation(libs.material)

    // Kotlin Coroutines
    runtimeOnly(libs.coroutines.android)

    // Retrofit
    implementation(libs.bundles.retrofit)

    // Kotlin Serialziation
    implementation(libs.bundles.kotlin.serialization)

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.expresso.core)
}