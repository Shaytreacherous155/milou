package com.santiifm.milou.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.santiifm.milou.R
import com.santiifm.milou.ui.components.common.CommonButton
import com.santiifm.milou.util.Constants


@Composable
fun SortButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    CommonButton(
        onClick = onClick,
        modifier = modifier,
        icon = painterResource(R.drawable.ic_sort),
        width = Constants.BUTTON_WIDTH_SMALL
    )
}

@Composable
fun FilterButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    CommonButton(
        onClick = onClick,
        modifier = modifier,
        icon = painterResource(R.drawable.ic_filter),
        width = Constants.BUTTON_WIDTH_SMALL
    )
}

@Composable
fun ConsoleFiltersButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    CommonButton(
        onClick = onClick,
        modifier = modifier,
        icon = painterResource(R.drawable.ic_gamepad),
        width = Constants.BUTTON_WIDTH_SMALL
    )
}

@Composable
fun ClearFiltersButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    CommonButton(
        onClick = onClick,
        modifier = modifier,
        icon = painterResource(R.drawable.ic_clear_filter),
        text = "Clear",
        width = Constants.BUTTON_WIDTH_MEDIUM
    )
}
