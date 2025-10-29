plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.exjunk.thanos.effect"

    defaultConfig {
        applicationId = "com.exjunk.thanos.effect"
        minSdk = 24
        targetSdk = 36
        compileSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    //implementation(libs.androidx.photopicker.compose)
    implementation (libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation3.ui.android)
    implementation(libs.androidx.compose.foundation)
    //implementation(libs.thanoseffect)
    implementation(project(":VanishEffect"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.material3)
    implementation(libs.android.material)
    implementation(libs.androidx.material3.window.size.class1)
    implementation(libs.androidx.material3.adaptive.navigation.suite)


    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}