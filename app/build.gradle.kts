import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
}

android {
    namespace = "es.timasostima.robank"
    compileSdk = 35

    val file = rootProject.file("local.properties")
    val properties =Properties()
    properties.load(file.inputStream())

    defaultConfig {
        applicationId = "es.timasostima.robank"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "GCM_SENDER_ID", "\"${properties.getProperty("gcm_defaultSenderId")}\"")
        buildConfigField("String", "GOOGLE_API_KEY", "\"${properties.getProperty("google_api_key")}\"")
        buildConfigField("String", "GOOGLE_APP_ID", "\"${properties.getProperty("google_app_id")}\"")
        buildConfigField("String", "GOOGLE_CRASH_KEY", "\"${properties.getProperty("google_crash_reporting_api_key")}\"")
        buildConfigField("String", "GOOGLE_STORAGE", "\"${properties.getProperty("google_storage_bucket")}\"")
        buildConfigField("String", "GOOGLE_SERVER_CLIENT_ID", "\"${properties.getProperty("google_server_client_id")}\"")
        buildConfigField("String", "FIREBASE_DATABASE", "\"${properties.getProperty("firebase_database_url")}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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

    implementation(libs.androidx.animation.graphics.android)
    implementation(libs.androidx.appcompat)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Charts and navigation
    implementation (libs.compose.charts)
    implementation(libs.androidx.credentials)
    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    //firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database);
    implementation(libs.play.services.auth)
    implementation(libs.googleid)

//  Dialogs
    implementation(libs.picker.core)
    implementation(libs.picker.color)
    implementation(libs.picker.input)
    implementation(libs.picker.state)

    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.drawablepainter)

    implementation (libs.coil.compose)
    implementation (libs.coil.gif)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)
}