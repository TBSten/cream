package me.tbsten.cream.test.callFrom

import me.tbsten.cream.CallFrom

data class StampArgs(
    val text: String,
)

// `suffix` does not match any StampArgs property and has a default -> it is omitted from the
// bridge, and the original default applies.
@CallFrom(StampArgs::class)
fun stamp(
    text: String,
    suffix: String = "!",
): String = text + suffix

data class BadgeArgs(
    val text: String,
    val suffix: String,
)

// `suffix` matches a BadgeArgs property -> the auto-copied default (`badgeArgs.suffix`) takes
// precedence over the original `"!"`.
@CallFrom(BadgeArgs::class)
fun badge(
    text: String,
    suffix: String = "!",
): String = text + suffix

data class TallyArgs(
    val label: String,
    val count: Int,
)

// @CallFrom.Exclude wins over both the auto-copied default and the original default: `count`
// stays in the bridge as a required parameter.
@CallFrom(TallyArgs::class)
fun tally(
    label: String,
    @CallFrom.Exclude count: Int = 0,
): String = "$label:$count"
