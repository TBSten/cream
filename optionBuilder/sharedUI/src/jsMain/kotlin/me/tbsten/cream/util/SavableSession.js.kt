package me.tbsten.cream.util

import kotlinx.browser.window
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.dom.url.URLSearchParams

private val json = Json {
    ignoreUnknownKeys = true
}

internal actual fun <T> getFromSavableSession(serializer: KSerializer<T>, key: String): T =
    getQueryParameter(key)
        ?.let { json.decodeFromString(serializer, it) }
        ?: error("no query parameter for $key")

private fun getQueryParameter(key: String): String? {
    val queryParameters = URLSearchParams(window.location.search)
    return queryParameters.get(key)
}

internal actual fun <T> setToSavableSession(
    serializer: KSerializer<T>,
    key: String,
    value: T
) {
    setQueryParameter(key, json.encodeToString(serializer, value))
}

private fun setQueryParameter(key: String, value: String) {
    val url = URL(window.location.toString())
    url.searchParams.set(key, value)

    window.history.replaceState(null, "", url.toString())
}
