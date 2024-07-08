package com.example.ancandroid.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ancandroid.openUrlInExternalBrowser

@Composable
fun ChurchInfoView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            "10000 Foothill Blvd.\nLake View Terrace, CA 91342",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            textAlign = TextAlign.Center
        )
        Button(
            onClick = {
                openUrlInExternalBrowser("https://anconnuri.com")
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Home Page")
        }
    }
}
