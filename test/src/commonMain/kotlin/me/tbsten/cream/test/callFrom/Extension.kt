package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class GreetExtArgs(
    val name: String,
)

@CallFrom(GreetExtArgs::class)
fun String.greetWith(name: String): String = "$this, $name"

data class PickArgs(
    val index: Int,
)

@CallFrom(PickArgs::class)
fun <T> List<T>.pickAt(index: Int): T = this[index]

data class OrValueArgs(
    val fallback: String,
)

@CallFrom(OrValueArgs::class)
suspend fun String?.orValue(fallback: String): String = this ?: fallback
