package com.santiifm.milou.ui.screens.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.santiifm.milou.R
import com.santiifm.milou.ui.components.ContactBlock
import com.santiifm.milou.ui.components.PresentationBlock

@Composable
fun ContactScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
        ) {
            val isPortrait = maxHeight > maxWidth

            if (isPortrait) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 150.dp, bottom = 200.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    PresentationBlock(
                        stringResource(R.string.username),
                        stringResource(R.string.occupation)
                    )
                    ContactBlock(
                        stringResource(R.string.username),
                        stringResource(R.string.username)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    PresentationBlock(
                        stringResource(R.string.username),
                        stringResource(R.string.occupation),
                        modifier = Modifier.padding(end = 20.dp)
                    )
                    ContactBlock(
                        stringResource(R.string.username),
                        stringResource(R.string.username),
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}