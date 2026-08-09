package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class JoinPlainArgs(
    // A non-array property never matches a vararg parameter, so `items` stays a required vararg.
    val items: String,
)

@CallFrom(JoinPlainArgs::class)
fun joinPlain(vararg items: String): String = items.joinToString("+")

data class JoinAllArgs(
    val items: Array<String>,
)

@CallFrom(JoinAllArgs::class)
fun joinAll(vararg items: String): String = items.joinToString("+")
