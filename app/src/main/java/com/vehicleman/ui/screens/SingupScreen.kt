package com.vehicleman.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vehicleman.ui.viewmodel.SingupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingupScreen(navController: NavController, viewModel: SingupViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSignupSuccessful) {
        if (uiState.isSignupSuccessful) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (uiState.isCodeSent) "Verify Email" else "Sign Up") },
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.isCodeSent) {
                VerificationContent(uiState, viewModel)
            } else {
                SignupFormContent(uiState, viewModel)
            }
        }
    }
}

@Composable
fun SignupFormContent(uiState: com.vehicleman.ui.viewmodel.SingupFormState, viewModel: SingupViewModel) {
    OutlinedTextField(value = uiState.username, onValueChange = viewModel::onUsernameChanged, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.firstName, onValueChange = viewModel::onFirstNameChanged, label = { Text("First Name") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.lastName, onValueChange = viewModel::onLastNameChanged, label = { Text("Last Name") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.address, onValueChange = viewModel::onAddressChanged, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.city, onValueChange = viewModel::onCityChanged, label = { Text("City") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.country, onValueChange = viewModel::onCountryChanged, label = { Text("Country") }, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(value = uiState.email, onValueChange = viewModel::onEmailChanged, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { viewModel.sendVerificationCode() }, modifier = Modifier.fillMaxWidth()) {
        Text("Send Verification Code")
    }
    uiState.errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
fun VerificationContent(uiState: com.vehicleman.ui.viewmodel.SingupFormState, viewModel: SingupViewModel) {
    Text("A 6-digit code has been sent to your email. Please enter it below.")
    Spacer(modifier = Modifier.height(16.dp))
    OutlinedTextField(
        value = uiState.verificationCode,
        onValueChange = viewModel::onVerificationCodeChanged,
        label = { Text("Verification Code") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(16.dp))
    Button(onClick = { viewModel.verifyCodeAndSignup() }, modifier = Modifier.fillMaxWidth()) {
        Text("Verify and Sign Up")
    }
    uiState.errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
    }
}