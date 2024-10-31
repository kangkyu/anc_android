package com.anconnuri.ancandroid.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.anconnuri.ancandroid.data.CountryCode
import com.anconnuri.ancandroid.data.countryCodes
import com.anconnuri.ancandroid.ui.theme.customBlue
import com.anconnuri.ancandroid.utils.NanpVisualTransformation
import com.anconnuri.ancandroid.viewmodel.AuthState
import com.anconnuri.ancandroid.viewmodel.PhoneAuthViewModel
import com.google.firebase.auth.FirebaseUser


@Composable
fun PhoneAuthScreen(
    viewModel: PhoneAuthViewModel,
    onLoginSuccess: (FirebaseUser?) -> Unit
) {
    val authState by viewModel.authState.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess((authState as AuthState.Success).user)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("휴대폰 인증", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold), color = customBlue)

        // Error message at the top if exists
        errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Main content
        when (authState) {
            is AuthState.CodeSent -> VerificationCodeInput(viewModel)
            is AuthState.Loading -> LoadingIndicator()
            else -> PhoneNumberInput(viewModel)
        }
    }
}

@Composable
fun PhoneNumberInput(viewModel: PhoneAuthViewModel) {
    val phoneNumber by viewModel.phoneNumber.collectAsState()
    val phoneNumberError by viewModel.phoneNumberError.collectAsState()
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()
    val placeholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
//            CountryCodeSelector(
//                selectedCountryCode = selectedCountryCode,
//                onSelectionChange = { viewModel.updateCountryCode(it) },
//            )
//            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { number ->
                    val stripped = number.replace(Regex("[^0-9]"), "")
                    viewModel.updatePhoneNumber(stripped.take(10))
                },
                label = { Text("휴대폰 번호 입력") },
                placeholder = { Text("(234) 567-8900", color = placeholderColor) },
                modifier = Modifier.weight(1f),
                isError = phoneNumberError != null,
                visualTransformation = NanpVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        }
        phoneNumberError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.sendVerificationCode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("확인", modifier = Modifier.padding(10.dp))
        }
    }
}

@Composable
fun VerificationCodeInput(viewModel: PhoneAuthViewModel) {
    val verificationCode by viewModel.verificationCode.collectAsState()
    val verificationCodeError by viewModel.verificationCodeError.collectAsState()

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
            label = { Text("인증번호 입력") },
            modifier = Modifier.fillMaxWidth(),
            isError = verificationCodeError != null,
        )
        verificationCodeError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.verifyCode() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("확인", modifier = Modifier.padding(10.dp))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryCodeSelector(
    selectedCountryCode: CountryCode,
    onSelectionChange: (CountryCode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            value = selectedCountryCode.prefix,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
                .width(96.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            countryCodes.forEach { countryCode ->
                DropdownMenuItem(
                    text = { Text("${countryCode.code} (${countryCode.prefix})") },
                    onClick = {
                        onSelectionChange(countryCode)
                        expanded = false
                    }
                )
            }
        }
    }
}
