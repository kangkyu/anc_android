package com.anconnuri.ancandroid.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anconnuri.ancandroid.R
import com.anconnuri.ancandroid.openUrlInExternalBrowser

@Composable
fun ChurchInfoView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .clickable {
                    openUrlInExternalBrowser("https://anconnuri.com")
                }
                .padding(vertical = 46.dp)
                .size(200.dp)
                .aspectRatio(1f),
            shadowElevation = 10.dp,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 22.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(22.dp, alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.width(56.dp),
                    tint = Color.Unspecified,
                    painter = painterResource(id = R.drawable.ic_house),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.width(157.dp),
                    painter = painterResource(id = R.drawable.img_church_home),
                    contentDescription = null
                )
            }
        }
        Text(
            "10000 Foothill Blvd.\nLake View Terrace, CA 91342",
            modifier = Modifier
                .clickable {
                    openUrlInExternalBrowser("https://maps.app.goo.gl/dHNZP3vGJDtU7d3L6")
                }
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}
