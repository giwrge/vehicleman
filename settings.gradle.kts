pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Προστέθηκε το JitPack για τρίτες βιβλιοθήκες, όπως η sqlcipher
        maven("https://jitpack.io")
    }
}
rootProject.name = "Vehicle Man"
include(":app")