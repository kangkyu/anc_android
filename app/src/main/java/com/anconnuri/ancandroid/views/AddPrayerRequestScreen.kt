package com.anconnuri.ancandroid.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.anconnuri.ancandroid.viewmodel.FormState
import com.anconnuri.ancandroid.viewmodel.PrayerViewModel
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddPrayerRequestScreen(navController: NavHostController) {
    val viewModel: PrayerViewModel = koinViewModel()
    var content by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    LaunchedEffect(formState) {
        when (formState) {
            is FormState.Submitted -> {
                delay(1000) // Show success message for 1 second
                viewModel.resetFormState()
                navController.popBackStack()
            }
            else -> {} // Do nothing for other states
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close"
            )
        }

        Text("기도제목을 보내세요", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Your prayer request") },
            modifier = Modifier.fillMaxWidth().height(200.dp),
            enabled = formState !is FormState.Submitting,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.None  // Prevents single-line behavior
            ),
            singleLine = false
        )

        Button(
            onClick = { viewModel.submitPrayerRequest(content) },
            modifier = Modifier.align(Alignment.End),
            enabled = content.isNotBlank() && formState !is FormState.Submitting
        ) {
            Text("Submit")
        }

        when (formState) {
            is FormState.Submitting -> CircularProgressIndicator()
            is FormState.Error -> {
                Text(
                    text = (formState as FormState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
            }
            is FormState.Submitted -> {
                Text(
                    "Prayer request submitted successfully!",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            is FormState.Idle -> {} // Do nothing for Idle state
        }
    }
}
