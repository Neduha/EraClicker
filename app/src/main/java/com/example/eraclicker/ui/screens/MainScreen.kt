package com.example.eraclicker.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.eraclicker.R
import com.example.eraclicker.ui.navigation.Screen
import com.example.eraclicker.viewmodel.GameViewModel
import kotlinx.coroutines.launch
import kotlin.random.Random
import com.example.eraclicker.util.NumberFormatter
import com.example.eraclicker.ui.theme.getAccentColorForEra

@Composable
fun MainScreen(
    navController: NavHostController,
    gameVm: GameViewModel
) {
    val bgRes = when (gameVm.currentEra) {
        0 -> R.drawable.bg_caveman
        1 -> R.drawable.bg_tribal
        2 -> R.drawable.bg_ancient
        3 -> R.drawable.bg_industrial
        4 -> R.drawable.bg_modern
        5 -> R.drawable.bg_postmodern
        6 -> R.drawable.bg_interstellar
        7 -> R.drawable.bg_ascended
        else -> R.drawable.bg_caveman
    }

    val clickRes = when (gameVm.currentEra) {
        0 -> R.drawable.co_caveman
        1 -> R.drawable.co_tribal
        2 -> R.drawable.co_ancient
        3 -> R.drawable.co_industrial
        4 -> R.drawable.co_modern
        5 -> R.drawable.co_postmodern
        6 -> R.drawable.co_interstellar
        7 -> R.drawable.co_ascended
        else -> R.drawable.co_caveman
    }

    val clicks = remember { mutableStateListOf<ClickAnim>() }
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val eraAccentColor = getAccentColorForEra(gameVm.currentEra)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = bgRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        clicks.forEach { anim ->
            Text(
                text = anim.text,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = anim.x, y = anim.y + anim.offsetY.value.dp)
                    .alpha(anim.alpha.value)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .pointerInput(Unit) {
                        detectTapGestures { offset: Offset ->
                            val gain = gameVm.clickPower
                            gameVm.onClickResource()

                            val xDp = with(density) { (offset.x - 100f).toDp() }
                            val yDp = with(density) { (offset.y - 100f).toDp() }

                            val id = System.currentTimeMillis()
                            val offsetY = Animatable(0f)
                            val alpha = Animatable(1f)
                            val clickAnim = ClickAnim(
                                id = id,
                                text = "+${NumberFormatter.formatGeneric(gain)}",
                                x = xDp + Random.nextInt(-20, 20).dp,
                                y = yDp + Random.nextInt(-20, 20).dp,
                                offsetY = offsetY,
                                alpha = alpha
                            )
                            clicks += clickAnim

                            scope.launch { offsetY.animateTo(-50f, tween(1000)) }
                            scope.launch {
                                alpha.animateTo(0f, tween(1000))
                                clicks.remove(clickAnim)
                            }
                        }
                    }
            ) {
                Image(
                    painter = painterResource(id = clickRes),
                    contentDescription = "Click target",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = NumberFormatter.format(gameVm.resources),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Passive: ${NumberFormatter.format(gameVm.passiveIncome)}/s",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Screen.Upgrades.route) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = eraAccentColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Upgrades")
                }

                Button(
                    onClick = { navController.navigate(Screen.Stats.route) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = eraAccentColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Stats")
                }
            }
        }
    }
}

private data class ClickAnim(
    val id: Long,
    val text: String,
    val x: Dp,
    val y: Dp,
    val offsetY: Animatable<Float, *>,
    val alpha: Animatable<Float, *>
)