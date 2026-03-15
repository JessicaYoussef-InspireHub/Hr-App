plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id ("com.google.gms.google-services")
    kotlin("kapt")
//    id("com.google.gms.google-services")
}

android {
    namespace = "net.inspirehub.hr"
    compileSdk = 36

    defaultConfig {
        applicationId = "net.inspirehub.hr"
        minSdk = 24
        targetSdk = 36
        versionCode = 15
        versionName = "1.0.6"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Enables code-related app optimization.
            isMinifyEnabled = true

            // Enables resource shrinking.
            isShrinkResources = true
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
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

    implementation (libs.androidx.activity.compose.v180)
    implementation (libs.androidx.lifecycle.viewmodel.compose) 
    implementation (libs.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.core.v352)

    implementation(libs.ktor.client.content.negotiation)  
    implementation(libs.ktor.ktor.serialization.kotlinx.json)  
    implementation(libs.ktor.client.okhttp) 
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.navigation.compose)  

    implementation (libs.play.services.location) 
    implementation (libs.accompanist.permissions) 

    implementation (libs.androidx.material.icons.extended) 

    implementation (libs.androidx.biometric)
    implementation(libs.androidx.appcompat)  

    implementation(libs.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.glide)
    implementation(libs.glide.compose)

    implementation(libs.androidx.core.splashscreen) 

    implementation (libs.androidx.work.runtime.ktx)

    implementation(platform(libs.firebase.bom))
    implementation (platform(libs.firebase.bom.v3211))
    implementation(libs.firebase.analytics)

    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore.ktx)

    implementation (libs.androidx.room.runtime)
//    kapt ("androidx.room:room-compiler:2.6.1")
    implementation (libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)

}