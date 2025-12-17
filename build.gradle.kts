plugins {
    // Android Build Tools
    id("com.android.application") version "8.13.2" apply false

    // Kotlin Compiler
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // Compose Compiler
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false

    // Dagger Hilt
    id("com.google.dagger.hilt.android") version "2.52" apply false

    // KAPT Plugin
    id("org.jetbrains.kotlin.kapt") version "2.0.0" apply false
}
