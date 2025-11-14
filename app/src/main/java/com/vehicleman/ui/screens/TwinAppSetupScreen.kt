package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.R
import com.vehicleman.domain.repositories.ProLevel
import com.vehicleman.domain.repositories.TwinAppRole
import com.vehicleman.ui.viewmodel.TwinAppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwinAppSetupScreen(
    navController: NavController,
    viewModel: TwinAppViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val user by viewModel.user.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = if (isNightMode) painterResource(id = R.mipmap.img_preferense_background_night) else painterResource(id = R.mipmap.img_preferense_background),
            contentDescription = "Background",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Twin App Setup") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
                // Check if the user is a pro user or in test mode to access this feature
                if (user.proLevel < ProLevel.PRO_1 && !user.isTestMode) {
                    Text("This is a PRO feature. Please upgrade to use Twin App.")
                } else {
                    // UI based on the user's Twin App role
                    when (user.twinAppRole) {
                        TwinAppRole.NONE -> {
                            Button(onClick = { viewModel.becomeMainDriver() }) {
                                Text("Become Main Driver")
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { 
                                viewModel.becomeSubDriver()
                                // In a real app, you would navigate to a QR scanner screen
                            }) {
                                Text("Scan QR to become Sub-Driver")
                            }
                        }
                        TwinAppRole.MAIN_DRIVER -> {
                            Text("You are the Main Driver.")
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Show this QR code to your Sub-Drivers (QR display not implemented yet).")
                        }
                        TwinAppRole.SUB_DRIVER -> {
                            Text("You are connected as a Sub-Driver.")
                        }
                    }
                }
            }
        }
    }
}