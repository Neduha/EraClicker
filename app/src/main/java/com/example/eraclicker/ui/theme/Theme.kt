package com.example.eraclicker.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import com.example.eraclicker.R

@Composable
fun EraClickerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val DarkColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.purple_80),
        secondary = colorResource(id = R.color.purple_grey_80),
        tertiary = colorResource(id = R.color.pink_80),
        background = colorResource(id = R.color.dark_background),
        surface = colorResource(id = R.color.dark_surface),
        onPrimary = colorResource(id = R.color.black_custom),
        onSecondary = colorResource(id = R.color.black_custom),
        onTertiary = colorResource(id = R.color.black_custom),
        onBackground = colorResource(id = R.color.white_custom),
        onSurface = colorResource(id = R.color.white_custom)
    )

    val LightColorScheme = lightColorScheme(
        primary = colorResource(id = R.color.purple_40),
        secondary = colorResource(id = R.color.purple_grey_40),
        tertiary = colorResource(id = R.color.pink_40),
        background = colorResource(id = R.color.light_background),
        surface = colorResource(id = R.color.light_surface),
        onPrimary = colorResource(id = R.color.white_custom),
        onSecondary = colorResource(id = R.color.white_custom),
        onTertiary = colorResource(id = R.color.white_custom),
        onBackground = colorResource(id = R.color.on_light_background_surface),
        onSurface = colorResource(id = R.color.on_light_background_surface)
    )

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}