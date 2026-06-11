# Standardizing Dependency Versions & Verifying Imports

This plan aims to synchronize the project's dependencies with the Version Catalog (`libs.versions.toml`) for better maintainability and to ensure all files have the necessary imports.

## Proposed Changes

### Build Configuration

#### [libs.versions.toml](file:///C:/Users/x/Desktop/vehicleman/gradle/libs.versions.toml)
- Update versions to stable and compatible releases.
- Add missing libraries and plugins that are currently hardcoded in `app/build.gradle.kts`.

#### [app/build.gradle.kts](file:///C:/Users/x/Desktop/vehicleman/app/build.gradle.kts)
- Replace hardcoded dependency strings with references from the Version Catalog (e.g., `implementation(libs.androidx.core.ktx)`).
- Sync Compose BOM and related libraries.

#### [build.gradle.kts](file:///C:/Users/x/Desktop/vehicleman/build.gradle.kts)
- Update plugin versions to match the new Version Catalog.

---

### Import Verification
- Perform a project-wide scan of all Kotlin files using `analyze_file` and `grep` to identify any "Unresolved reference" errors.
- Automatically add missing imports where identified.

## Verification Plan

### Automated Tests
- `gradlew assembleDebug`: Verify the project builds successfully after dependency refactoring.
- `analyze_file`: Run on a sample of key files to ensure no syntax/import errors remain.

### Manual Verification
- Deploy the app to a device/emulator to ensure runtime stability with the new library versions.
