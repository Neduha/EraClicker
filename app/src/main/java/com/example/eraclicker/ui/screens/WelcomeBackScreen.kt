package com.example.eraclicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.eraclicker.ui.theme.getAccentColorForEra
import com.example.eraclicker.util.NumberFormatter
import com.example.eraclicker.viewmodel.GameViewModel

@Composable
fun WelcomeBackScreen(
    gameVm: GameViewModel,
    timeAway: String,
    resourcesEarned: Long,
    onCollectClicked: () -> Unit
) {
    val currentEraIndex = gameVm.currentEra
    val eraAccentColor = getAccentColorForEra(currentEraIndex)
    val rimSize = 8.dp
    val contentCornerRadius = 16.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(eraAccentColor, shape = RoundedCornerShape(contentCornerRadius))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(rimSize)
                    .clip(RoundedCornerShape(contentCornerRadius - rimSize.coerceAtMost(contentCornerRadius)))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineLarge,
                        color = eraAccentColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Text(
                        text = "You were away for:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = timeAway,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Text(
                        text = "You earned:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${NumberFormatter.formatGeneric(resourcesEarned)} resources!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = eraAccentColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    Button(
                        onClick = onCollectClicked,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eraAccentColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(50.dp)
                    ) {
                        Text(
                            "COLLECT & CONTINUE",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}