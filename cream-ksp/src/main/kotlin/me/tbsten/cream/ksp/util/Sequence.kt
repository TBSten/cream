package me.tbsten.cream.ksp.util

internal fun <T> Sequence<T>.isCountMoreThan(count: Int, include: Boolean): Boolean {
    if (count < 0) return true // 負の数よりは常に多い
    val threshold = if (include) count else count + 1
    var currentCount = 0
    for (e in this) {
        currentCount++
        if (currentCount >= threshold) return true
    }
    return false
}

internal fun <T> Sequence<T>.isCountLessThan(count: Int, include: Boolean): Boolean {
    if (count < 0) return false // 負の数より少なくなることはない
    val threshold = if (include) count else count - 1
    if (threshold < 0) return false // 0未満の閾値より少なくなることはない (空のシーケンスの場合を除くが、その場合は currentCount < 0 となるので問題ない)
    var currentCount = 0
    for (e in this) {
        currentCount++
        if (currentCount > threshold) return false
    }
    return true
}
