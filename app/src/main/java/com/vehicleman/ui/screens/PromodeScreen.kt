package com.vehicleman.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.ui.viewmodel.PromodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromodeScreen(navController: NavController, viewModel: PromodeViewModel = hiltViewModel()) {
    var showPaymentDialog by remember { mutableStateOf<ProLevel?>(null) }
    val context = LocalContext.current

    if (showPaymentDialog != null) {
        PaymentConfirmationDialog(
            level = showPaymentDialog!!,
            onConfirm = {
                viewModel.upgradeToPro(it)
                showPaymentDialog = null
                navController.popBackStack()
            },
            onDismiss = { showPaymentDialog = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Upgrade to Pro") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProPlanCard(
                level = ProLevel.PRO_1,
                price = "€1.99",
                features = listOf(
                    "4 to 7 vehicles",
                    "Backup and Restore",
                    "2 users (TwinApp)",
                    "Import from other apps"
                ),
                onUpgrade = { showPaymentDialog = ProLevel.PRO_1 }
            )
            ProPlanCard(
                level = ProLevel.PRO_2,
                price = "€9.99",
                features = listOf(
                    "8 to 10 vehicles",
                    "Backup and Restore",
                    "7 users (TwinApp)",
                    "Import & Export data"
                ),
                onUpgrade = { showPaymentDialog = ProLevel.PRO_2 }
            )
            ProPlanCard(
                level = ProLevel.PRO_3,
                price = "Contact us",
                features = listOf(
                    "Unlimited everything",
                    "Priority support"
                ),
                buttonText = "Contact",
                onUpgrade = {
                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:supevisor.vehicleman@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "Vehicle Man - Promode 3 Inquiry")
                        putExtra(Intent.EXTRA_TEXT, "Παρακαλώ επικοινωνήστε μαζί μου. Ενδιαφέρομαι για την πρόσβαση στην εφαρμογή Vehicle Man σε Promode 3 (no limits).")
                    }
                    context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                }
            )
        }
    }
}

@Composable
fun ProPlanCard(
    level: ProLevel,
    price: String,
    features: List<String>,
    buttonText: String = "Upgrade Now",
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = level.name.replace("_", " "), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = price, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            features.forEach {
                Text(text = "• $it", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onUpgrade, modifier = Modifier.fillMaxWidth()) {
                Text(buttonText)
            }
        }
    }
}

@Composable
fun PaymentConfirmationDialog(level: ProLevel, onConfirm: (ProLevel) -> Unit, onDismiss: () -> Unit) {
    val price = if (level == ProLevel.PRO_1) "€1.99" else "€9.99"
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Purchase") },
        text = { Text("Do you want to purchase ${level.name.replace("_", " ")} for $price?") },
        confirmButton = {
            TextButton(onClick = { onConfirm(level) }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}