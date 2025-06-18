plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.scrolltracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ScrollTracker.scrolltracker"
        minSdk = 24
        targetSdk = 35
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
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(libs.jackson.databind) // or any latest version
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.core)
    implementation(libs.fragment)

    // For Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // ViewPager2 for carousel
    implementation(libs.viewpager2)

    // RecyclerView for app list
    implementation(libs.recyclerview)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}