package me.tbsten.cream.test.copyToFun

import me.tbsten.cream.CopyTo

// Basic usage: Use a factory function to create target
@CopyTo.Fun(funName = "createMyTarget")
data class MySource(
    val a: String,
    val b: Int,
)

data class MyTarget(
    val a: String,
    val b: Int,
    val c: Boolean,
)

fun createMyTarget(
    a: String,
    b: Int,
    c: Boolean,
): MyTarget = MyTarget(a = a, b = b, c = c)
