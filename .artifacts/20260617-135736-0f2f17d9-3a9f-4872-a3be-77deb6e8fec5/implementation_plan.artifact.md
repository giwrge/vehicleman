# Implementation Plan - Clear All Data Except Vehicles

This plan outlines the steps to add a feature that allows users to delete all records (fuel, service, reminders) and drivers from the database while preserving the vehicle entries.

## Proposed Changes

### Domain Layer

#### [NEW] [ClearAllDataExceptVehiclesUseCase.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/domain/use_case/ClearAllDataExceptVehiclesUseCase.kt)

- Create a new Use Case that interacts with `RecordRepository`, `DriverRepository`, and `VehicleRepository`.
- The `invoke` method will:
    1. Call `recordRepository.deleteAllRecords()`.
    2. Call `driverRepository.deleteAllDrivers()`.
    3. Call `driverRepository.deleteAllCrossRefs()`.
    4. Fetch all vehicles and reset their `recordCount` to 0.

### Presentation Layer

#### [PreferenceViewModel.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/presentation/preference/PreferenceViewModel.kt)

- Inject `ClearAllDataExceptVehiclesUseCase`.
- Add a `clearAllData()` method that launches a coroutine to execute the Use Case.

### UI Layer

#### [PreferenceScreen.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/ui/screens/PreferenceScreen.kt)

- Add a state variable `showClearDataDialog` to manage the visibility of the confirmation dialog.
- Add an `AlertDialog` for confirmation before performing the deletion.
- Add a new `PreferenceCard` in the "Database Management" section with the label "Διαγραφή εγγραφών και χρηστών" (Delete records and users).

---

## Verification Plan

### Automated Tests
- I will check if the build passes after the changes.

### Manual Verification
1. Open the **Settings** screen.
2. Scroll to **Database Management**.
3. Tap on **Διαγραφή εγγραφών και χρηστών**.
4. Confirm the action in the dialog.
5. Verify that:
    - All fuel/service records are gone.
    - All drivers are deleted.
    - Vehicles still exist in the main list.
    - Vehicle sorting by "Most Entries" works correctly (all should have 0 entries).
