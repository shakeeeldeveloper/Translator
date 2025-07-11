plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt) // ✅ Add this line here

}

android {
    namespace = "com.example.translatorproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.translatorproject"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures{
        viewBinding=true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.12.0")

    // ML Kit Translation
    implementation ("com.google.mlkit:translate:17.0.1")
    implementation ("com.google.mlkit:language-id:17.0.4")
    implementation ("com.google.mlkit:text-recognition:16.0.0")



// Speech Recognizer & TTS (default Android APIs)
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")


    implementation ("androidx.camera:camera-core:1.3.1")
    implementation ("androidx.camera:camera-camera2:1.3.1")
    implementation ("androidx.camera:camera-lifecycle:1.3.1")
    implementation ("androidx.camera:camera-view:1.3.1")

    implementation ("androidx.fragment:fragment-ktx:1.6.2")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

// Room components
    implementation ("androidx.room:room-runtime:2.6.1")

    kapt("androidx.room:room-compiler:2.6.1") //

// Kotlin extensions and coroutines support
    implementation ("androidx.room:room-ktx:2.6.1")

// Lifecycle ViewModel & LiveData (already likely present)
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

// Optional - for Java 8+ features (e.g., Instant, LocalDate)
    implementation ("androidx.room:room-guava:2.6.1")


    implementation ("com.vanniktech:android-image-cropper:4.5.0")
   // implementation ("com.canhub:android-image-cropper:4.6.1")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    kapt ("com.github.bumptech.glide:compiler:4.16.0")





}