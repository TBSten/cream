package me.tbsten.cream.util

import kotlinx.serialization.KSerializer

private var savedMap = mutableMapOf<String, Any?>()

internal actual fun <T> getFromSavableSession(
    serializer: KSerializer<T>,
    key: String
): T {
    if (!savedMap.contains(key)) error("Savable session does not have $key")
    return savedMap[key] as T
}

internal actual fun <T> setToSavableSession(
    serializer: KSerializer<T>,
    key: String,
    value: T
) {
    savedMap[key] = value
}