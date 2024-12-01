plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.mlapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mlapp"
        minSdk = 28
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    // Ml model for image classification
    implementation("com.google.mlkit:image-labeling:17.0.9")
    // custom Ml model for flower classification
    implementation("com.google.mlkit:image-labeling-custom:17.0.3")
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}