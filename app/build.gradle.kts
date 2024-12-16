plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pokedexjavaapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pokedexjavaapp"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    implementation (libs.gson)
    implementation(libs.cardview)
    implementation (libs.recyclerview)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.androidx.espresso.contrib)
    implementation(libs.androidx.databinding.compiler)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core)
    testImplementation(libs.espresso.core)
    testImplementation(libs.ext.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.picasso)
    implementation(libs.androidx.core.splashscreen)
    implementation (libs.mpandroidchart)
    implementation(libs.androidx.palette)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)
    testImplementation(libs.mockito.core)
    androidTestImplementation(libs.androidx.junit.v115)
    testImplementation(libs.robolectric)
    testImplementation ("org.powermock:powermock-module-junit4:2.0.4")
    testImplementation ("org.powermock:powermock-api-mockito2:2.0.4")
}