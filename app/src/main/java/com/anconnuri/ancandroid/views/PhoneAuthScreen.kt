package com.anconnuri.ancandroid.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.anconnuri.ancandroid.navigation.Screens
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin


@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    // UI elements and logic based on the states
    when (authState) {
        is AuthState.Loading -> LoadingIndicator()
        is AuthState.Error -> ErrorMessage((authState as AuthState.Error).message)
        is AuthState.Success -> onLoginSuccess()
        is AuthState.CodeSent -> VerificationCodeInput(viewModel)
        is AuthState.Idle -> PhoneNumberInput(viewModel)
    }
}

@Composable
fun PhoneNumberInput(viewModel: PhoneAuthViewModel) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { number ->
                viewModel.updatePhoneNumber(number)
            },
            label = { Text("Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.sendVerificationCode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Send Verification Code")
        }
    }
}

@Composable
fun VerificationCodeInput(viewModel: PhoneAuthViewModel) {
    val verificationCode by viewModel.verificationCode.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = verificationCode,
            onValueChange = { code -> viewModel.updateVerificationCode(code) },
            label = { Text("Verification Code") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.verifyCode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verify Code")
        }
    }
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(message)
    }
}

@Composable
fun NavigateToNextScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Temporary Test - NavigateToNextScreen")
    }
}
