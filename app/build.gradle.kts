import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
    id("kotlin-parcelize")

}

val localProperties = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}

android {
    namespace = "com.example.akirax"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.akirax"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TMDB_API_KEY", "\"${localProperties["tmdbApiKey"]}\"")
        buildConfigField("String", "TICKETMASTER_API_KEY", "\"${localProperties["ticketmasterApiKey"]}\"")
        buildConfigField("String", "CASHFREE_CLIENT_ID", "\"${localProperties["cashfreeClientId"]}\"")
        buildConfigField("String", "CASHFREE_CLIENT_SECRET", "\"${localProperties["cashfreeClientSecret"]}\"")
        buildConfigField("String", "FIREBASE_WEB_CLIENT_ID", "\"${localProperties["firebaseWebClientId"]}\"")

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
    implementation(libs.play.services.auth)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.customview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))

    // When using the BoM, don't specify versions in Firebase dependencies
    implementation("com.google.firebase:firebase-analytics")
    // Add the dependencies for any other desired Firebase products
    // https://firebase.google.com/docs/android/setup#available-libraries

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")
    // Firebase coroutines(for auth and credential manager stuff )
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    // Declare the dependency for the Cloud Firestore library
    implementation("com.google.firebase:firebase-firestore")


    //Koin
    val koin_version = "3.5.0"
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose:$koin_version")
    implementation("io.insert-koin:koin-androidx-compose-navigation:$koin_version")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    //Material Icons extended
    implementation("androidx.compose.material:material-icons-extended:1.7.3")

    //Google OAuth
    implementation ("com.google.android.gms:play-services-auth:21.2.0")

    //Credential Manager
    implementation ("androidx.credentials:credentials:1.3.0")
    implementation ("androidx.credentials:credentials-play-services-auth:1.3.0")
    implementation ("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    //Older method:
    //implementation ("com.google.android.libraries.identity:google-identity-credentials:1.0.1")

    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6")

    //Navigation component
    implementation("androidx.navigation:navigation-compose:2.8.3")

    //Coil
    implementation("io.coil-kt:coil-compose:2.7.0")

    //Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //Kotlinx Serialization
    implementation ("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    //CashFree
    implementation ("com.cashfree.pg:api:2.1.27")

    //implementation("org.web3j:core:4.8.7") // Web3j Dependency
    //implementation("org.web3j:android:4.9.3")

    //ZXing QRCode Generator
    implementation("com.google.zxing:core:3.5.1")

    //SplashScreenApi
    implementation ("androidx.core:core-splashscreen:1.0.1")

    //Accompanist pager
    implementation ("com.google.accompanist:accompanist-pager:0.29.0-alpha")


}

