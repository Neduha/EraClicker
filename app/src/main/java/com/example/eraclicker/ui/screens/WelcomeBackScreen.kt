package com.example.eraclicker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun WelcomeBackScreen(
    timeAway: String,
    resourcesEarned: Long,
    onCollectClicked: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )
            Text(
                text = "You were away for:",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = timeAway,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = "You earned:",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$resourcesEarned resources!",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            Button(onClick = onCollectClicked) {
                Text("COLLECT & CONTINUE")
            }
        }
    }
}
