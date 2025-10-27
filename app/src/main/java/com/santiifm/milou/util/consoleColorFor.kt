package com.santiifm.milou.util

import androidx.compose.ui.graphics.Color
import com.santiifm.milou.ui.theme.LightText
import com.santiifm.milou.ui.theme.LightningBlue
import com.santiifm.milou.ui.theme.NeonGreen

fun consoleColorFor(console: String): Color = when(console) {
    "Xbox 360" -> NeonGreen
    "Nintendo 64" -> Color.Gray
    "Sega Genesis" -> LightningBlue
    else -> LightText
}
