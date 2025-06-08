package com.example.eraclicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.concurrent.TimeUnit
import androidx.compose.ui.res.colorResource
import com.example.eraclicker.R

@Composable
fun TimeTravelerLockoutScreen(
    lockoutMessage: String,
    remainingMillis: Long,

) {

    val hours = TimeUnit.MILLISECONDS.toHours(remainingMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMillis) % 60

    val countdownString = String.format("%02d:%02d:%02d", hours, minutes, seconds)

    val rimSize = 8.dp
    val contentCornerRadius = 16.dp

    val primaryContentColor = MaterialTheme.colorScheme.onErrorContainer

    val titleAccentColor = colorResource(id = R.color.paradox_accent_color)

    val rimBackgroundColor = colorResource(id = R.color.paradox_accent_color).copy(alpha = 0.6f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(rimBackgroundColor, shape = RoundedCornerShape(contentCornerRadius))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(rimSize)
                    .clip(RoundedCornerShape(contentCornerRadius - rimSize.coerceAtMost(contentCornerRadius)))
                    .background(MaterialTheme.colorScheme.errorContainer)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp, vertical = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "WARNING: PARADOX DETECTED!",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = titleAccentColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = lockoutMessage,
                        style = MaterialTheme.typography.bodyLarge,
                        color = primaryContentColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Time remaining to stabilize reality:",
                        style = MaterialTheme.typography.titleMedium,
                        color = primaryContentColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = countdownString,
                        fontSize = 52.sp,
                        style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                        color = titleAccentColor,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}