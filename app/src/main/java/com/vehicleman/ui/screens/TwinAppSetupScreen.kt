package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.g0dkar.qrcode.QRCode
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.ui.viewmodel.TwinAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwinAppSetupScreen(navController: NavController, viewModel: TwinAppViewModel = hiltViewModel()) {
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Twin App Setup") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (user.twinAppRole) {
                TwinAppRole.NONE -> {
                    if (user.proLevel >= ProLevel.PRO_1) {
                        Button(onClick = { viewModel.becomeMainDriver() }) {
                            Text("Become Main Driver")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { 
                            viewModel.becomeSubDriver()
                            navController.popBackStack() // Simulate scan and return
                        }) {
                            Text("Scan QR to become Sub-Driver")
                        }
                    } else {
                        Text("You need to be a Pro user to use Twin App.")
                    }
                }
                TwinAppRole.MAIN_DRIVER -> {
                    Text("You are the Main Driver. Show this QR code to your Sub-Drivers.")
                    Spacer(modifier = Modifier.height(16.dp))
                    // In a real app, the content would be a unique pairing ID
                    QRCode(content = user.email, modifier = Modifier.size(200.dp))
                }
                TwinAppRole.SUB_DRIVER -> {
                    Text("You are connected as a Sub-Driver.")
                }
            }
        }
    }
}