package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class AddArgs(
    val other: Int,
)

class Counter(
    val value: Int,
) {
    @CallFrom(AddArgs::class)
    operator fun plus(other: Int): Counter = Counter(value + other)
}

data class JoinWithArgs(
    val other: String,
)

class Joiner(
    val value: String,
) {
    @CallFrom(JoinWithArgs::class)
    infix fun join(other: String): String = value + other
}

data class RunTaggedArgs(
    val tag: String,
)

@CallFrom(RunTaggedArgs::class)
inline fun runTagged(
    tag: String,
    block: () -> String,
): String = tag + block()

data class CountDownArgs(
    val n: Int,
)

@CallFrom(CountDownArgs::class)
tailrec fun countDown(n: Int): Int = if (n <= 0) 0 else countDown(n - 1)
