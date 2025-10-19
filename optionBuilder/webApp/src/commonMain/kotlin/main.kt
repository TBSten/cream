@file:Suppress("ktlint:standard:filename")

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import me.tbsten.cream.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() =
    ComposeViewport(
        configure = { isA11YEnabled = true },
    ) { App() }
