package me.tbsten.cream.util

import androidx.compose.runtime.*
import kotlinx.serialization.KSerializer

/**
 * Web では クエリパラメータにJVM ではアプリ内に アプリ内ストレージに状態を保存する。
 *  に。
 */
@Composable
fun <T> rememberSavableSessionState(
    key: String,
    serializer: KSerializer<T>,
    calculation: @DisallowComposableCalls () -> T,
) = remember(key1 = key, calculation = {
    runCatching {
        getFromSavableSession<T>(serializer = serializer, key = key)
    }.getOrElse { calculation() }
        .let(::mutableStateOf)
}).also {
    LaunchedEffect(it.value) {
        runCatching {
            setToSavableSession(serializer, key, it.value)
        }.onFailure {
            it.printStackTrace()
        }
    }
}

internal expect fun <T> getFromSavableSession(serializer: KSerializer<T>, key: String): T
internal expect fun <T> setToSavableSession(serializer: KSerializer<T>, key: String, value: T)
