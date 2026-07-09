package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class ProcessDataArgs(
    val data1: String,
    val data2: Int,
)

@CallFrom(ProcessDataArgs::class)
fun processData(
    data1: String,
    data2: Int,
): String = "$data1-$data2"

data class GreetArgs(
    val name: String,
)

@CallFrom(GreetArgs::class)
fun greet(
    name: String,
    punctuation: String,
): String = "Hello, $name$punctuation"

data class LoadArgs(
    val id: String,
)

@CallFrom(LoadArgs::class)
suspend fun load(id: String): Int = id.length

data class MultiplyArgs(
    val value: Int,
)

class Calculator(
    private val factor: Int,
) {
    @CallFrom(MultiplyArgs::class)
    fun multiply(value: Int): Int = value * factor
}
