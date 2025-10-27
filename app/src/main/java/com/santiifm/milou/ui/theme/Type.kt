package com.santiifm.milou.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.santiifm.milou.R

val MilouFontFamily = FontFamily(
    Font(R.font.space_mono)
)
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = MilouFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    titleLarge = TextStyle(
        fontFamily = MilouFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    labelSmall = TextStyle(
        fontFamily = MilouFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    displayLarge = TextStyle(
        fontFamily = MilouFontFamily
    ),
    bodyMedium = TextStyle(
        fontFamily = MilouFontFamily
    ),
    bodySmall = TextStyle(
        fontFamily = MilouFontFamily
    ),
    titleMedium = TextStyle(
        fontFamily = MilouFontFamily
    ),
    titleSmall = TextStyle(
        fontFamily = MilouFontFamily
    )
)