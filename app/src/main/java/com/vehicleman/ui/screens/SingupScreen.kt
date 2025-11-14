package com.vehicleman.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.vehicleman.ui.viewmodel.SingupFormState
import com.vehicleman.ui.viewmodel.SingupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingupScreen(
    navController: NavController,
    viewModel: SingupViewModel = hiltViewModel(),
    isNightMode: Boolean
) {
    val uiState by viewModel.uiState.collectAsState()

    // Navigate back on successful signup
    LaunchedEffect(uiState.isSignupSuccessful) {
        if (uiState.isSignupSuccessful) {
            navController.popBackStack()
        }
    }

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
                    title = { Text("Sign Up") },
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
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    if (!uiState.isCodeSent) {
                        SignupForm(uiState = uiState, viewModel = viewModel)
                    } else {
                        VerificationForm(uiState = uiState, viewModel = viewModel)
                    }
                }

                uiState.errorMessage?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = it)
                }
            }
        }
    }
}

@Composable
private fun SignupForm(uiState: SingupFormState, viewModel: SingupViewModel) {
    Text("Enter your details to sign up.")
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.username,
        onValueChange = viewModel::onUsernameChanged,
        label = { Text("Username") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.email,
        onValueChange = viewModel::onEmailChanged,
        label = { Text("Email") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.firstName,
        onValueChange = viewModel::onFirstNameChanged,
        label = { Text("First Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.lastName,
        onValueChange = viewModel::onLastNameChanged,
        label = { Text("Last Name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.address,
        onValueChange = viewModel::onAddressChanged,
        label = { Text("Address") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.city,
        onValueChange = viewModel::onCityChanged,
        label = { Text("City") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = uiState.country,
        onValueChange = viewModel::onCountryChanged,
        label = { Text("Country") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { viewModel.sendVerificationCode() },
        enabled = uiState.username.isNotBlank() && uiState.email.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Send Verification Code")
    }
}

@Composable
private fun VerificationForm(uiState: SingupFormState, viewModel: SingupViewModel) {
    Text("A verification code was sent to ${uiState.email}. Please enter it below.")
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.verificationCode,
        onValueChange = viewModel::onVerificationCodeChanged,
        label = { Text("Verification Code") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { viewModel.verifyCodeAndSignup() },
        enabled = uiState.verificationCode.isNotBlank(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Verify and Sign Up")
    }
}