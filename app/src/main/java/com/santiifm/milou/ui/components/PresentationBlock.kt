package com.santiifm.milou.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.santiifm.milou.R
import com.santiifm.milou.ui.theme.WarmOrange


@Composable
fun PresentationBlock(name: String, occupation: String, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        PresentationImage(modifier)
        PresentationText(name, occupation, modifier)
    }
}

@Composable
fun PresentationImage(modifier: Modifier = Modifier){
    val image = painterResource(R.drawable.my_avatar)
    Image(
        painter = image,
        contentDescription = "Avatar",
        modifier = modifier
            .size(200.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(
                    3.dp,
                    WarmOrange
                ),
                CircleShape
            ),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun PresentationText(name: String, occupation: String, modifier: Modifier = Modifier){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ){
        Text(
            text = name,
            fontSize = 40.sp,
            lineHeight = 42.sp,
            textDecoration = TextDecoration.Underline,
            modifier = modifier
        )
        Text(
            text = occupation,
            fontSize = 15.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = WarmOrange,
        )
    }
}