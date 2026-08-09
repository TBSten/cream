package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class MakeArgs(
    val value: String,
)

class Factory {
    companion object {
        @CallFrom(MakeArgs::class)
        fun make(value: String): String = "made:$value"
    }
}

typealias UserId = String

data class ResolveArgs(
    val id: UserId,
)

@CallFrom(ResolveArgs::class)
fun resolve(id: String): String = "resolved:$id"

data class FailArgs(
    val message: String,
)

@CallFrom(FailArgs::class)
fun failWith(message: String): Nothing = throw IllegalStateException(message)

data class FindArgs(
    val id: String,
)

@CallFrom(FindArgs::class)
fun find(id: String): String? = id.ifEmpty { null }

data class LegacyArgs(
    val value: String,
)

@Deprecated("Use processV2 instead.")
@CallFrom(LegacyArgs::class)
fun legacyProcess(value: String): String = "legacy:$value"
