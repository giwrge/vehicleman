package com.vehicleman.ui.screens // Corrected package

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.SubDriverType
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.domain.repositories.VehicleSortOrder
import com.vehicleman.ui.navigation.NavDestinations
import com.vehicleman.ui.viewmodel.PreferencesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceScreen(
    navController: NavController,
    viewModel: PreferencesViewModel = hiltViewModel(),
    fromScreen: String?,
    fromId: String?
) {
    val isNightMode by viewModel.isNightMode.collectAsState(initial = false)
    val coroutineScope = rememberCoroutineScope()
    var showSortDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showLegalDialog by remember { mutableStateOf(false) }
    val vehicleSortOrder by viewModel.vehicleSortOrder.collectAsState(initial = VehicleSortOrder.ALPHABETICAL)
    val user by viewModel.user.collectAsState()

    val isSingleSubDriver = user.twinAppRole == TwinAppRole.SUB_DRIVER && user.subDriverType == SubDriverType.SINGLE

    if (showSortDialog) {
        SortOrderDialog(
            currentSortOrder = vehicleSortOrder,
            onDismiss = { showSortDialog = false },
            onSortOrderSelected = { sortOrder ->
                if (sortOrder == VehicleSortOrder.CUSTOM) {
                    navController.navigate(NavDestinations.CUSTOM_SORT_ROUTE)
                } else {
                    viewModel.setVehicleSortOrder(sortOrder)
                }
                showSortDialog = false
            }
        )
    }

    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            onExport = { format -> viewModel.exportData(format) }
        )
    }

    if (showAppInfoDialog) {
        AppInfoDialog(onDismiss = { showAppInfoDialog = false })
    }

    if (showContactDialog) {
        ContactDialog(onDismiss = { showContactDialog = false })
    }

    if (showLegalDialog) {
        LegalDialog(onDismiss = { showLegalDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        var totalDragAmount by remember { mutableStateOf(0f) }
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { totalDragAmount = 0f },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            totalDragAmount += dragAmount
                        },
                        onDragEnd = {
                            if (totalDragAmount < -30) { // Swipe Left
                                val route = when (fromScreen) {
                                    NavDestinations.HOME_IDENTIFIER -> NavDestinations.HOME_ROUTE
                                    NavDestinations.RECORD_IDENTIFIER -> NavDestinations.entryListRoute(fromId ?: "")
                                    else -> NavDestinations.HOME_ROUTE // Default fallback
                                }
                                navController.navigate(route)
                            }
                        }
                    )
                }
        ) {
            // General
            Text(text = "General", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Night Mode", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Enable dark theme", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = isNightMode, onCheckedChange = { 
                        coroutineScope.launch {
                            viewModel.setNightMode(it)
                        }
                    })
                }
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Test Mode", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Bypass Pro restrictions", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = user.isTestMode, onCheckedChange = { 
                        viewModel.setTestMode(it)
                    })
                }
            }
            val isPro = user.proLevel != ProLevel.NONE || user.isTestMode
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable(enabled = isPro && !isSingleSubDriver) { showSortDialog = true }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Modify Vehicle List Sorting", style = MaterialTheme.typography.bodyLarge, color = if (isPro && !isSingleSubDriver) Color.Unspecified else Color.Gray)
                        Text(text = "Change how vehicles are displayed in the main list", style = MaterialTheme.typography.bodySmall, color = if (isPro && !isSingleSubDriver) Color.Unspecified else Color.Gray)
                    }
                    if (!isPro) {
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                    }
                }
            }

            if (!isSingleSubDriver) { // Hide Data & Sync for SingleSubDrivers
                // Data & Sync
                Text(text = "Data & Sync", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = isPro) { navController.navigate(NavDestinations.BACKUP_ROUTE) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Backup", style = MaterialTheme.typography.bodyLarge, color = if (isPro) Color.Unspecified else Color.Gray)
                            Text(text = "Save a backup of your data", style = MaterialTheme.typography.bodySmall, color = if (isPro) Color.Unspecified else Color.Gray)
                        }
                        if (!isPro) {
                             Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(enabled = isPro) { navController.navigate(NavDestinations.RESTORE_ROUTE) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Restore", style = MaterialTheme.typography.bodyLarge, color = if (isPro) Color.Unspecified else Color.Gray)
                            Text(text = "Restore your data from a backup", style = MaterialTheme.typography.bodySmall, color = if (isPro) Color.Unspecified else Color.Gray)
                        }
                         if (!isPro) {
                             Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
                val isPro1 = user.proLevel >= ProLevel.PRO_1 || user.isTestMode
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = isPro1) { navController.navigate(NavDestinations.IMPORT_WIZARD_ROUTE) }) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Import Data", style = MaterialTheme.typography.bodyLarge, color = if (isPro1) Color.Unspecified else Color.Gray)
                            Text(text = "Transfer data from supported apps", style = MaterialTheme.typography.bodySmall, color = if (isPro1) Color.Unspecified else Color.Gray)
                        }
                         if (!isPro1) {
                             Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
                val isPro2 = user.proLevel >= ProLevel.PRO_2 || user.isTestMode
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(enabled = isPro2) { showExportDialog = true }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Export Data", style = MaterialTheme.typography.bodyLarge, color = if (isPro2) Color.Unspecified else Color.Gray)
                            Text(text = "Export your data to an external file", style = MaterialTheme.typography.bodySmall, color = if (isPro2) Color.Unspecified else Color.Gray)
                        }
                         if (!isPro2) {
                             Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
            }

            if (!isSingleSubDriver) { // Hide User & Device Management for SingleSubDrivers
                // User & Device Management
                Text(text = "User & Device Management", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate(NavDestinations.SIGN_UP_ROUTE) }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Sign up user", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Free user becomes a registered user", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable(enabled = isPro) { navController.navigate(NavDestinations.DRIVERS_ROUTE) }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Manage Drivers", style = MaterialTheme.typography.bodyLarge, color = if (isPro) Color.Unspecified else Color.Gray)
                            Text(text = "Assign vehicles to drivers for statistics", style = MaterialTheme.typography.bodySmall, color = if (isPro) Color.Unspecified else Color.Gray)
                        }
                        if (!isPro) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
                 Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable(enabled = isPro) { navController.navigate(NavDestinations.TWIN_APP_SETUP_ROUTE) }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Twin App", style = MaterialTheme.typography.bodyLarge, color = if (isPro) Color.Unspecified else Color.Gray)
                            Text(text = "Sync data with a second device in real time", style = MaterialTheme.typography.bodySmall, color = if (isPro) Color.Unspecified else Color.Gray)
                        }
                        if (!isPro) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                        }
                    }
                }
            }

            // About
            Text(text = "About", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showAppInfoDialog = true }) {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "App Info", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Version, manufacturer, etc.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { showContactDialog = true }) {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Contact Info", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Email, website, etc.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { showLegalDialog = true }) {
                 Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Legal", style = MaterialTheme.typography.bodyLarge)
                        Text(text = "Terms of Use, Privacy Policy, etc.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Pro Mode
            if (user.proLevel == ProLevel.NONE) {
                Text(text = "Pro Mode", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { navController.navigate(NavDestinations.PRO_MODE_ROUTE) }) {
                     Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Upgrade to Pro", style = MaterialTheme.typography.bodyLarge)
                            Text(text = "Unlock all exclusive features", style = MaterialTheme.typography.bodySmall)
                        }
                        Icon(imageVector = Icons.Default.Star, contentDescription = "Pro", tint = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun SortOrderDialog(
    currentSortOrder: VehicleSortOrder,
    onDismiss: () -> Unit,
    onSortOrderSelected: (VehicleSortOrder) -> Unit
) {
    val sortOptions = listOf(
        VehicleSortOrder.ALPHABETICAL to "Alphabetical",
        VehicleSortOrder.BY_DATE_ADDED to "By Date Added",
        VehicleSortOrder.MOST_ENTRIES to "Most Entries",
        VehicleSortOrder.BY_LAST_MODIFIED to "By Last Modified",
        VehicleSortOrder.CUSTOM to "Custom"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Sort Order") },
        text = {
            Column {
                sortOptions.forEach { (sortOrder, text) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (currentSortOrder == sortOrder),
                                onClick = { onSortOrderSelected(sortOrder) }
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentSortOrder == sortOrder),
                            onClick = { onSortOrderSelected(sortOrder) }
                        )
                        Text(
                            text = text,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExportDialog(onDismiss: () -> Unit, onExport: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Export Data") },
        text = { Text("Select format to export.") }, // Placeholder
        confirmButton = {
            TextButton(onClick = { 
                onExport("JSON") // Hardcoded for now
                onDismiss()
            }) {
                Text("JSON")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AppInfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("VehicleMan App") },
        text = {
            Column {
                Text("Version: 1.0.0")
                Text("Manufacturer: VehicleMan Dev Team")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Composable
fun ContactDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Contact Us") },
        text = { Text("For support or inquiries, please contact us by email.") },
        confirmButton = {
            TextButton(onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:supevisor.vehicleman@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Vehicle Man Support Inquiry")
                }
                context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                onDismiss()
            }) {
                Text("Send Email")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LegalDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Legal Information") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Copyright", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("© 2024 VehicleMan Dev Team. All rights reserved.")
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Terms of Use", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("By using VehicleMan ('the App'), you agree to be bound by these terms. You are granted a limited, non-exclusive, non-transferable, revocable license to use the App for your personal, non-commercial purposes, subject to these terms.")
                Spacer(modifier = Modifier.height(16.dp))

                Text("Privacy Policy (GDPR)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Our App stores all data locally on your device. We do not collect, transmit, or store your personal data on any external servers. Data entered, including vehicle details and records, remains under your control. Backups are created and stored at locations of your choosing.")
                Spacer(modifier = Modifier.height(16.dp))

                Text("Security", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("All application data is stored in the App's private directory on your device, which is protected by the Android Operating System's security measures. It is not accessible by other applications.")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
