package me.tbsten.cream.util

import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer

/**
 * Web では クエリパラメータにJVM ではアプリ内に アプリ内ストレージに状態を保存する。
 */
@Composable
fun <T> rememberShareableState(
    key: String,
    serializer: KSerializer<T>,
    calculation: @DisallowComposableCalls () -> T,
) = remember(key1 = key, calculation = {
    runCatching {
        getFromShareableState<T>(serializer = serializer, key = key)
    }.getOrElse { calculation() }
        .let(::mutableStateOf)
}).also {
    LaunchedEffect(it.value) {
        // Debounce URL updates to avoid excessive history.replaceState calls
        delay(DEBOUNCE_DELAY_MS)
        runCatching {
            setToShareableState(serializer, key, it.value)
        }.onFailure {
            it.printStackTrace()
        }
    }
}

private const val DEBOUNCE_DELAY_MS = 300L

internal expect fun <T> getFromShareableState(serializer: KSerializer<T>, key: String): T
internal expect fun <T> setToShareableState(serializer: KSerializer<T>, key: String, value: T)
