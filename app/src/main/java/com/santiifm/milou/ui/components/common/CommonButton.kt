package com.santiifm.milou.ui.components.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun CommonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: Painter? = null,
    text: String? = null,
    width: Int? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Button(
        onClick = onClick,
        modifier = modifier.let { 
            if (width != null) it.width(width.dp) else it 
        },
        contentPadding = contentPadding
    ) {
        icon?.let { 
            Icon(
                painter = it,
                contentDescription = text ?: "Button"
            )
        }
        text?.let { 
            Text(
                text = it,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
