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
import androidx.compose.material3.TopAppBarDefaults
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
import com.vehicleman.ui.viewmodel.PromodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromodeScreen(navController: NavController, viewModel: PromodeViewModel = hiltViewModel(), isNightMode: Boolean) {
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = { Text("Pro Mode") },
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
                Text("Current Pro Level: ${user.proLevel}")
                Spacer(modifier = Modifier.height(32.dp))

                if (user.proLevel < ProLevel.PRO_1) {
                    Button(onClick = { viewModel.upgradeToPro(ProLevel.PRO_1) }) {
                        Text("Upgrade to PRO 1")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (user.proLevel < ProLevel.PRO_2) {
                    Button(onClick = { viewModel.upgradeToPro(ProLevel.PRO_2) }) {
                        Text("Upgrade to PRO 2")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (user.proLevel < ProLevel.PRO_3) {
                    Button(onClick = { viewModel.upgradeToPro(ProLevel.PRO_3) }) {
                        Text("Upgrade to PRO 3")
                    }
                }

                if (user.proLevel == ProLevel.PRO_3) {
                    Text("You have the highest Pro Level!")
                }
            }
        }
    }
}