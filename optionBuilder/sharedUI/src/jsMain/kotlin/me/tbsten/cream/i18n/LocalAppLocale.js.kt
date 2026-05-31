package me.tbsten.cream.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.browser.window

private val localeState = staticCompositionLocalOf { systemLanguageCode() }

@Composable
internal actual fun provideAppLocale(localeTag: String): ProvidedValue<*> {
    // Read back by the `Navigator.prototype.languages` shim installed in index.html.
    window.asDynamic().__customLocale = localeTag.replace('_', '-')
    return localeState.provides(localeTag)
}

internal actual fun systemLanguageCode(): String = window.navigator.language.substringBefore('-')
