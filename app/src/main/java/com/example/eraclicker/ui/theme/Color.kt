package com.example.eraclicker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.example.eraclicker.R
import androidx.compose.runtime.Composable



@Composable
fun getAccentColorForEra(eraIndex: Int): Color {

    return when (eraIndex) {
        0 -> colorResource(id = R.color.era_accent_caveman)
        1 -> colorResource(id = R.color.era_accent_tribal)
        2 -> colorResource(id = R.color.era_accent_ancient)
        3 -> colorResource(id = R.color.era_accent_industrial)
        4 -> colorResource(id = R.color.era_accent_digital)
        5 -> colorResource(id = R.color.era_accent_post_digital)
        6 -> colorResource(id = R.color.era_accent_interstellar)
        7 -> colorResource(id = R.color.era_accent_ascended)
        else -> colorResource(id = R.color.default_era_accent)
    }
}

