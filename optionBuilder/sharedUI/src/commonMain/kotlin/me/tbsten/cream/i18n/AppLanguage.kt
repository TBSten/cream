package me.tbsten.cream.i18n

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Languages the Option Builder UI can be displayed in.
 *
 * - [localeTag] drives the runtime platform-locale override (see [LocalAppLocale]).
 * - [label] is the autonym shown in the language switcher. It is intentionally left untranslated
 *   so that users can recognise their own language regardless of the currently active one.
 *
 * The [SerialName] of each entry pins the serialized form, so the persisted / shareable value
 * (URL query parameter on web) stays stable even if the Kotlin entry names are later renamed.
 */
@Serializable
enum class AppLanguage(
    val localeTag: String,
    val label: String,
) {
    @SerialName("en")
    English("en", "English"),

    @SerialName("ja")
    Japanese("ja", "日本語"),
    ;

    companion object {
        /**
         * Resolves a language code or BCP-47-ish tag (e.g. `"ja"`, `"en-US"`) to a supported
         * [AppLanguage], falling back to [English] for anything unsupported.
         */
        fun fromLanguageCode(code: String): AppLanguage {
            val language = code.substringBefore('-')
            return entries.firstOrNull { it.localeTag.equals(language, ignoreCase = true) } ?: English
        }
    }
}

/**
 * The current system / browser UI language code (e.g. `"ja"`, `"en"`), used as the initial value
 * before the user makes an explicit choice. Implemented per platform because reading the system
 * locale is not part of common Kotlin and must not be a composable call.
 */
internal expect fun systemLanguageCode(): String
