plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.scrolltracker"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.scrolltracker"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2") // or any latest version
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
}