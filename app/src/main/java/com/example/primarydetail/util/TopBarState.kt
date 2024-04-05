package com.example.primarydetail.util

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null,
    val navigationAction: (@Composable () -> Unit)? = null,
    val numSelectedItems: Int = 0,
)
