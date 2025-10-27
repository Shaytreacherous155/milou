package com.santiifm.milou.ui.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.santiifm.milou.util.Constants


@Composable
fun VerticalSpacer(
    modifier: Modifier = Modifier,
    height: Dp = Constants.SECTION_SPACING_DP.dp
) {
    Spacer(modifier = modifier.height(height))
}

@Composable
fun HorizontalSpacer(
    modifier: Modifier = Modifier,
    width: Dp = Constants.BUTTON_SPACING_DP.dp
) {
    Spacer(modifier = modifier.width(width))
}