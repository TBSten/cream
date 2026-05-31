package me.tbsten.cream.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key

/**
 * Applies [language] to the composition. [key] forces a full recomposition of [content] whenever
 * the language changes, so every `stringResource` call inside re-resolves against the new locale.
 *
 * Note: language-independent state (options, class names, …) must be hoisted *above* this call so
 * it survives the keyed recomposition.
 *
 * Based on the official approach at
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html — each platform
 * applies the override differently (the JVM sets the default [java.util.Locale]; the browser swaps
 * the value read by the `navigator.languages` shim in `index.html`).
 */
@Composable
fun AppEnvironment(
    language: AppLanguage,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        provideAppLocale(language.localeTag),
    ) {
        key(language) {
            content()
        }
    }
}

/**
 * Platform hook: switches the locale that Compose Resources reads. The actual override is a side
 * effect performed here *during composition*, so it is applied before children read their strings.
 * The returned [ProvidedValue] only satisfies [CompositionLocalProvider]; re-resolution of
 * `stringResource` is driven by [AppEnvironment]'s [key], not by reading this value.
 */
@Composable
internal expect fun provideAppLocale(localeTag: String): ProvidedValue<*>
