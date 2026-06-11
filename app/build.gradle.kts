plugins {
    alias(libs.plugins.vmanAgp)
    alias(libs.plugins.vmanKgp)
    alias(libs.plugins.vmanKapt)
    alias(libs.plugins.vmanHilt)
}

android {
    namespace = "com.vehicleman"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vehicleman"
        minSdk = 26 
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
        sourceCompatibility = JavaVersion.VERSION_17 
        targetCompatibility = JavaVersion.VERSION_17 
    }
    kotlinOptions {
        jvmTarget = "17" 
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform(libs.androidxComposeBom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Android Core & Splash
    implementation(libs.androidxCoreKtx)
    implementation(libs.androidxCoreSplashscreen)

    // Lifecycle & ViewModel
    implementation(libs.androidxLifecycleRuntimeKtx)
    implementation(libs.androidxLifecycleRuntimeCompose)
    implementation(libs.androidxLifecycleViewmodelKtx)

    // Compose
    implementation(libs.androidxActivityCompose)
    implementation(libs.androidxComposeUi)
    implementation(libs.androidxComposeUiGraphics)
    implementation(libs.androidxComposeUiToolingPreview)
    implementation(libs.androidxComposeMaterial3)
    implementation(libs.androidxComposeMaterialIconsExtended)
    implementation(libs.composeReorderableLib)
    implementation(libs.ychartsLib)

    // DataStore & Gson
    implementation(libs.datastorePreferencesLib)
    implementation(libs.googleGsonLib)

    // Compose Tooling
    debugImplementation(libs.androidxComposeUiTooling)
    debugImplementation(libs.androidxComposeUiTestManifest)

    // Hilt
    implementation(libs.hiltAndroidLib)
    kapt(libs.hiltCompilerLib)
    implementation(libs.hiltNavigationComposeLib)

    // Room
    implementation(libs.roomRuntimeLib)
    kapt(libs.roomCompilerLib)
    implementation(libs.roomKtxLib)

    // Testing
    testImplementation(libs.junitLib)
    androidTestImplementation(libs.androidxJunitLib)
    androidTestImplementation(libs.androidxEspressoCoreLib)
    androidTestImplementation(libs.androidxComposeUiTestJunit4)
}
