# Ενίσχυση Add/Edit Record με Έξυπνες Λειτουργίες (v2)

## User Review Required

> [!IMPORTANT]
> - **Λογική Μετάφρασης**: Η μετάφραση θα γίνεται πλέον **μόνο** όταν ο χρήστης επιλέγει ένα suggestion ή όταν "φεύγει" από το πεδίο του τίτλου (onFocusChanged), για να μην ενοχλεί κατά την πληκτρολόγηση.
> - **Επαναφορά Τύπου**: Η οθόνη θα κρύβει αυτόματα τα πεδία υπενθύμισης αν η ημερομηνία αλλάξει από μελλοντική σε τωρινή/παρελθοντική.

## Proposed Changes

### 1. Presentation Layer (ViewModel)

#### [AddEditRecordViewModel.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/presentation/addeditrecord/AddEditRecordViewModel.kt)
- **Διόρθωση `processIntelligentTitle`**: Αφαίρεση της αυτόματης αλλαγής του `state.title` κατά την πληκτρολόγηση. Η μετάφραση θα αποθηκεύεται σε προσωρινή μεταβλητή ή θα εφαρμόζεται μόνο μέσω συγκεκριμένου event.
- **Βελτίωση `onEvent(DateChanged)`**: Πλήρης επαναφορά του `recordType` και των σχετικών flags (`showReminderFields`) όταν η ημερομηνία αλλάζει από μέλλον σε παρόν.
- **Νέο Event `TitleFocusLost`**: Για την εφαρμογή της μετάφρασης και του auto-fill όταν ο χρήστης τελειώσει την πληκτρολόγηση.

---

### 2. UI Layer (Screen)

#### [AddEditRecordScreen.kt](file:///C:/Users/x/Desktop/vehicleman/app/src/main/java/com/vehicleman/ui/screens/AddEditRecordScreen.kt)
- **[DELETE] QuickActionsToolbar**: Αφαίρεση των κουμπιών πάνω από το πληκτρολόγιο.
- **Focus Handling**: Προσθήκη `onFocusChanged` στο πεδίο του τίτλου για την ενεργοποίηση του έξυπνου parsing.
- **UI State Toggle**: Διασφάλιση ότι η οθόνη ανταποκρίνεται άμεσα στις αλλαγές του `recordType` από το ViewModel.

## Verification Plan

### Manual Verification
- **Σενάριο Καυσίμου**: Εισαγωγή "Βενζίνη 50€ 1.85" -> Έλεγχος αν συμπληρώθηκαν σωστά τα lt, η τιμή/lt και αν ο τίτλος έγινε "Αγορά Καυσίμου".
- **Σενάριο Υπενθύμισης**: Επιλογή μελλοντικής ημερομηνίας -> Έλεγχος αν ο τύπος άλλαξε αυτόματα σε Υπενθύμιση.
- **Επεξεργασιμότητα**: Επιβεβαίωση ότι τα πεδία lt και €/lt είναι επεξεργάσιμα μετά την αυτόματη συμπλήρωση.
- **UI Έλεγχος**: Έλεγχος εμφάνισης κουμπιών πάνω από το πληκτρολόγιο και σωστής θέσης των suggestions.
