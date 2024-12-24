package github.rikacelery.mikumusicx

import androidx.compose.ui.graphics.Color

data class VMState(
    val dynamicColor: Boolean = true,
    val seedColor: Color = Color.Green,
    val darkMode: Int = 0,
)
