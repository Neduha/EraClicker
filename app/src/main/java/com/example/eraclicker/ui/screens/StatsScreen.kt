package com.example.eraclicker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.eraclicker.ui.navigation.Screen
import com.example.eraclicker.ui.theme.getAccentColorForEra
import com.example.eraclicker.util.NumberFormatter
import com.example.eraclicker.viewmodel.GameViewModel
import androidx.compose.material3.HorizontalDivider

fun formatMillisToTimeString(millis: Long): String {
    if (millis < 0) return "N/A"
    if (millis == 0L) return "0s"

    val totalSeconds = millis / 1000
    val days = totalSeconds / (24 * 3600)
    val hours = (totalSeconds % (24 * 3600)) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return buildString {
        if (days > 0) append("${days}d ")
        if (hours > 0 || days > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0 || days > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()
}

@Composable
fun StatsScreen(
    navController: NavHostController,
    gameVm: GameViewModel
) {
    val offlineTimeMillis = gameVm.totalOfflineTimeMillis
    val totalManualClicks = gameVm.totalManualClicks
    val totalResourcesEverEarned = gameVm.totalResourcesEverEarned
    val totalResourcesFromClicks = gameVm.totalResourcesFromClicks
    val totalResourcesFromPassiveOrOffline = (totalResourcesEverEarned - totalResourcesFromClicks).coerceAtLeast(0L)

    val liveDisplayOnlineTimeMillis by produceState(initialValue = gameVm.getDisplayTotalOnlineTimeMillis()) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            value = gameVm.getDisplayTotalOnlineTimeMillis()
        }
    }

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
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Game Statistics",
                            style = MaterialTheme.typography.headlineMedium,
                            color = eraAccentColor,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        val statsList = listOf(
                            "Time Spent Online" to formatMillisToTimeString(liveDisplayOnlineTimeMillis),
                            "Time Spent Offline" to formatMillisToTimeString(offlineTimeMillis),
                            "Current Resources" to NumberFormatter.formatGeneric(gameVm.resources),
                            "Click Power" to NumberFormatter.formatGeneric(gameVm.clickPower),
                            "Passive Income (per sec)" to NumberFormatter.formatGeneric(gameVm.passiveIncome),
                            "Total Taps" to NumberFormatter.formatGeneric(totalManualClicks),
                            "Lifetime Resources Earned" to NumberFormatter.formatGeneric(totalResourcesEverEarned),
                            "Resources from Clicks" to NumberFormatter.formatGeneric(totalResourcesFromClicks),
                            "Resources from Passive/Offline" to NumberFormatter.formatGeneric(totalResourcesFromPassiveOrOffline)
                        )

                        statsList.forEachIndexed { index, statPair ->
                            StatItem(
                                label = statPair.first,
                                value = statPair.second,
                                valueColor = eraAccentColor
                            )
                            if (index < statsList.size - 1) {
                                HorizontalDivider(
                                    color = eraAccentColor.copy(alpha = 0.3f),
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp),
                                    thickness = 1.dp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate(Screen.Main.route) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = eraAccentColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    ) {
                        Text("Back to Game")
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = valueColor
        )
    }
}