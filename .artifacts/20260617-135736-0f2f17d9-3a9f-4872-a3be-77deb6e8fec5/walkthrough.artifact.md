# Walkthrough - Clear All Data Feature

I have implemented the "Clear All Data" feature, which allows you to delete all records and drivers while keeping your vehicles intact.

## Changes Made

### Core Logic
- Created [ClearAllDataExceptVehiclesUseCase.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/domain/use_case/ClearAllDataExceptVehiclesUseCase.kt) to handle the deletion of records and drivers, and to reset the `recordCount` on all vehicles.

### ViewModel
- Updated [PreferenceViewModel.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/presentation/preference/PreferenceViewModel.kt) to include the `clearAllData()` function.

### UI
- Updated [PreferenceScreen.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/ui/screens/PreferenceScreen.kt) by adding a new button "Διαγραφή εγγραφών και χρηστών" in the "Database Management" section.
- Added a confirmation dialog to prevent accidental data loss.

## How to use
1. Go to **Settings**.
2. Find the **Database Management** section.
3. Tap on **Διαγραφή εγγραφών και χρηστών**.
4. A dialog will appear. Tap **Διαγραφή** to confirm.

## Verification Summary
- The code was analyzed for errors and is syntactically correct.
- The Use Case follows the existing repository patterns.
- The UI integration matches the app's design language.
