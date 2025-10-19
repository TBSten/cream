package me.tbsten.cream.util

import kotlinx.browser.window
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

private val json =
    Json {
        ignoreUnknownKeys = true
    }

internal actual fun <T> getFromShareableState(
    serializer: KSerializer<T>,
    key: String,
): T =
    getQueryParameter(key)
        ?.let { paramValue ->
            runCatching { json.decodeFromString(serializer, paramValue) }
                .getOrElse {
                    console.error("Failed to decode parameter '$key': ${it.message}")
                    error("Invalid or malformed query parameter for $key")
                }
        }
        ?: error("no query parameter for $key")

private fun getQueryParameter(key: String): String? {
    val queryParameters = URLSearchParams(window.location.search)
    return queryParameters.get(key)
}

internal actual fun <T> setToShareableState(
    serializer: KSerializer<T>,
    key: String,
    value: T,
) {
    val encoded = json.encodeToString(serializer, value)
    // Validate that the encoded value is not excessively long
    if (encoded.length > MAX_URL_PARAM_LENGTH) {
        console.warn("Parameter '$key' is too large (${encoded.length} chars), not saving to URL")
        return
    }
    setQueryParameter(key, encoded)
}

private fun setQueryParameter(
    key: String,
    value: String,
) {
    val url = URL(window.location.toString())
    url.searchParams.set(key, value)

    // Check total URL length to prevent browser issues
    val newUrl = url.toString()
    if (newUrl.length > MAX_URL_LENGTH) {
        console.warn("URL would be too long (${newUrl.length} chars), not updating")
        return
    }

    window.history.replaceState(null, "", newUrl)
}

private const val MAX_URL_PARAM_LENGTH = 2000
private const val MAX_URL_LENGTH = 8000
