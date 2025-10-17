package com.vehicleman.presentation.addeditvehicle

/**
 * Περιγράφει τα Events που μπορούν να προκύψουν μέσα στο AddEditVehiclePanel
 * και τα οποία διαχειρίζεται το ViewModel.
 *
 * Αυτά τα events στέλνονται από το UI (HomeScreen) προς το ViewModel.
 */
sealed class AddEditVehiclePanelEvent {

    /** Εμφάνιση / Απόκρυψη Airflow Card για συγκεκριμένο όχημα **/
    data class ToggleAirflowCard(val vehicleId: String) : AddEditVehiclePanelEvent()

    /** Διαγραφή συγκεκριμένου οχήματος **/
    data class DeleteVehicleById(val vehicleId: String) : AddEditVehiclePanelEvent()

    /** Ανανέωση λίστας οχημάτων **/
    object RefreshVehicleList : AddEditVehiclePanelEvent()

    /** Εμφάνιση modal επιβεβαίωσης διαγραφής **/
    data class ConfirmDelete(val vehicleId: String) : AddEditVehiclePanelEvent()

    /** Απόρριψη modal / ακύρωση **/
    object CancelDelete : AddEditVehiclePanelEvent()
}
