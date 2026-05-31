package me.tbsten.cream.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import java.util.Locale

private val localeState = staticCompositionLocalOf { Locale.getDefault().toString() }

@Composable
internal actual fun provideAppLocale(localeTag: String): ProvidedValue<*> {
    val locale = Locale.forLanguageTag(localeTag)
    // Process-global mutation; acceptable for this single-window tool app.
    Locale.setDefault(locale)
    return localeState.provides(locale.toString())
}

internal actual fun systemLanguageCode(): String = Locale.getDefault().language
