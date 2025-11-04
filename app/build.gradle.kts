plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "net.inspirehub.hr"
    compileSdk = 35

    defaultConfig {
        applicationId = "net.inspirehub.hr"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        compose = true
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
    implementation (libs.androidx.lifecycle.viewmodel.compose)//done
    implementation (libs.core)
    implementation(libs.zxing.android.embedded)
    implementation(libs.core.v352)

    implementation(libs.ktor.client.content.negotiation) //done
    implementation(libs.ktor.ktor.serialization.kotlinx.json) //done
    implementation(libs.ktor.client.okhttp)//done
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.androidx.navigation.compose) //done

    implementation (libs.play.services.location)//done
    implementation (libs.accompanist.permissions)//done

    implementation (libs.androidx.material.icons.extended)//done

    implementation (libs.androidx.biometric)
    implementation(libs.androidx.appcompat) //done

    implementation(libs.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.glide)//done
    implementation(libs.glide.compose)

    implementation(libs.androidx.core.splashscreen)//done

    implementation (libs.androidx.work.runtime.ktx) //done

}