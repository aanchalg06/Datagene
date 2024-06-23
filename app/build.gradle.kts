plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
//    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.hackthon_datallm_ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.hackthon_datallm_ai"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    // Import the Firebase BoM
//    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
//
//    // When using the BoM, you don't specify versions in Firebase library dependencies
//
//    // Add the dependency for the Firebase SDK for Google Analytics
//    //noinspection UseTomlInstead
//    implementation("com.google.firebase:firebase-analytics")
    implementation ("io.coil-kt:coil-gif:2.0.0-rc02")
    implementation ("io.coil-kt:coil-compose:2.2.2")
    implementation ("com.opencsv:opencsv:5.9")
    implementation ("org.apache.commons:commons-csv:1.10.0")
    implementation ("androidx.compose.runtime:runtime-livedata:1.6.6")
    implementation( "androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.1.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}