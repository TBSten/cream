package me.tbsten.cream.test.mutableCopyTo

import me.tbsten.cream.MutableCopyTo

@MutableCopyTo(MutableTargetWithOptions::class, mutableCopyFunNamePrefix = "updateWith")
data class MutableSourceWithOptions(
    val prop1: String,
    val prop2: Int,
)

data class MutableTargetWithOptions(
    var prop1: String,
    var prop2: Int,
    var extraProp: String,
)
