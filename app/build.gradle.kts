plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt") // Required for Hilt & Room
    id("com.google.dagger.hilt.android") // Hilt plugin
}

android {
    namespace = "com.vehicleman"
    compileSdk = 34 // Targeting SDK 34 is better for modern Compose

    defaultConfig {
        applicationId = "com.vehicleman"
        minSdk = 26 // ΑΛΛΑΓΗ: Αυξήθηκε από 24 σε 26 για συμβατότητα με τα adaptive icons
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
        sourceCompatibility = JavaVersion.VERSION_17 // ΑΛΛΑΓΗ: Ορίζουμε Java 17
        targetCompatibility = JavaVersion.VERSION_17 // ΑΛΛΑΓΗ: Ορίζουμε Java 17
    }
    kotlinOptions {
        jvmTarget = "17" // ΑΛΛΑΓΗ: Ορίζουμε Kotlin JVM Target 17
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1" // Align with Compose BOM
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Compose BOM - This ensures all Compose libraries are compatible
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Android Core
    implementation("androidx.core:core-ktx:1.12.0")

    // FIX: Splash Screen API
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3") // For collectAsStateWithLifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")

    // Compose - No versions needed here, they are managed by the BOM
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended") // For Icons like Add, Delete, etc.
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation("co.yml:ycharts:2.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Compose Tooling
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.52")
    kapt("com.google.dagger:hilt-android-compiler:2.52")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Room (Database)
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1") // Coroutine support

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
}
