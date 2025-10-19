package me.tbsten.cream.theme

import androidx.compose.runtime.Composable

@Composable
internal actual fun PlatformAppTheme(content: @Composable (() -> Unit)) {
    FontProvider { content() }
}
