package com.santiifm.milou.ui.components

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santiifm.milou.R
import com.santiifm.milou.util.Constants


@Composable
fun ContactBlock(linkedin: String, github: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        ContactText(linkedin, github, modifier)
    }
}

@Composable
fun ContactText(linkedin: String, github: String, modifier: Modifier = Modifier){
    val uriHandler = LocalUriHandler.current
    val infiniteTransition = rememberInfiniteTransition(label = "magicCarpet")

    val offsetY by infiniteTransition.animateFloat(
        initialValue = -Constants.FLOAT_ANIMATION_OFFSET,
        targetValue = Constants.FLOAT_ANIMATION_OFFSET,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = Constants.ANIMATION_DURATION_MS, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .padding(bottom = 20.dp)
    ){
        IconLink(
            icon = painterResource(id = R.drawable.ic_linkedin),
            text = "LinkedIn",
            onClick = { uriHandler.openUri("https://www.linkedin.com/in/$linkedin") },
            modifier = Modifier
                .graphicsLayer {
                    translationY = offsetY
                }
                .padding(bottom = 5.dp)
        )
        IconLink(
            icon = painterResource(id = R.drawable.ic_github),
            text = "GitHub",
            onClick = { uriHandler.openUri("https://github.com/$github") },
            modifier = Modifier
                .padding(bottom = 20.dp)
                .graphicsLayer {
                    translationY = offsetY
                }
        )
    }
}

@Composable
private fun IconLink(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "$text logo",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
        )
    }
}