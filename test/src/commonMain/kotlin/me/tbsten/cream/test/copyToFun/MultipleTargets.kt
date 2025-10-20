package me.tbsten.cream.test.copyToFun

import me.tbsten.cream.CopyTo

// Multiple targets with different factory functions
@CopyTo.Fun(funName = "createTarget1")
@CopyTo.Fun(funName = "createTarget2")
data class MultiSource(
    val value: String,
)

data class Target1(
    val value: String,
    val extra1: Int,
)

data class Target2(
    val value: String,
    val extra2: Boolean,
)

fun createTarget1(
    value: String,
    extra1: Int,
): Target1 = Target1(value = value, extra1 = extra1)

fun createTarget2(
    value: String,
    extra2: Boolean,
): Target2 = Target2(value = value, extra2 = extra2)
