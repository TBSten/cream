package me.tbsten.cream.util

import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun <T> adaptive(small: T, medium: T = small, large: T = medium): T = adaptive(
    small = { small },
    medium = { medium },
    large = { large },
)

@Composable
fun <T> adaptive(
    small: () -> T,
    medium: () -> T = small,
    large: () -> T = medium,
    @Suppress("unused") disableTrailingLambda: Nothing? = null,
): T {
    val breakpoint = currentBreakpoint()
    return when (breakpoint) {
        Breakpoint.Small -> small()
        Breakpoint.Medium -> medium()
        Breakpoint.Large -> large()
    }
}

@Composable
fun currentBreakpoint(): Breakpoint {
    val screenWidth =
        with(LocalDensity.current) {
            LocalWindowInfo.current.containerSize.width.toDp()
        }
    return Breakpoint.fromWidth(screenWidth)
}

enum class Breakpoint(val maxWidth: Dp) {
    Small(600.dp),
    Medium(1200.dp),
    Large(Dp.Infinity),
    ;

    operator fun <T> invoke(block: () -> T) = DataOverBreakpoint(this, block)
    inner class DataOverBreakpoint<T> internal constructor(val breakpoint: Breakpoint, val data: () -> T)

    companion object {
        fun fromWidth(width: Dp) = entries.firstOrNull {
            width <= it.maxWidth
        } ?: entries.last()
    }
}

@Composable
fun AdaptiveContainer(
    small: @Composable (content: @Composable () -> Unit) -> Unit,
    medium: @Composable (content: @Composable () -> Unit) -> Unit = small,
    large: @Composable (content: @Composable () -> Unit) -> Unit = medium,
    content: @Composable () -> Unit,
) = adaptive(
    small = small,
    medium = medium,
    large = large,
).let {
    it.invoke { content() }
}

@Composable
fun <S> AdaptiveContainer(
    small: @Composable (content: @Composable S.() -> Unit) -> Unit,
    medium: @Composable (content: @Composable S.() -> Unit) -> Unit = small,
    large: @Composable (content: @Composable S.() -> Unit) -> Unit = medium,
    content: @Composable S.() -> Unit,
) = adaptive(
    small = small,
    medium = medium,
    large = large,
).let {
    it.invoke { content() }
}

@Composable
fun SmallColumnMediumRow(
    space: Dp = 0.dp,
    content: @Composable ColumnOrRowScope.() -> Unit,
) = AdaptiveContainer<ColumnOrRowScope>(
    small = { Column(verticalArrangement = Arrangement.spacedBy(space)) { it(ColumnOrRowScope(columnScope = this)) } },
    medium = { Row(horizontalArrangement = Arrangement.spacedBy(space)) { it(ColumnOrRowScope(rowScope = this)) } },
    content = {
        content()
    },
)

@Composable
fun MediumColumnLargeRow(
    space: Dp = 0.dp,
    content: @Composable ColumnOrRowScope.() -> Unit,
) = AdaptiveContainer<ColumnOrRowScope>(
    small = { Column(verticalArrangement = Arrangement.spacedBy(space)) { it(ColumnOrRowScope(columnScope = this)) } },
    large = { Row(horizontalArrangement = Arrangement.spacedBy(space)) { it(ColumnOrRowScope(rowScope = this)) } },
    content = {
        content()
    },
)

class ColumnOrRowScope internal constructor(
    private val columnScope: ColumnScope? = null,
    private val rowScope: RowScope? = null,
) {
    @Stable
    fun Modifier.weight(
        @FloatRange(from = 0.0, fromInclusive = false) weight: Float,
        fill: Boolean = true,
    ): Modifier = then(
        if (columnScope != null) {
            with(columnScope) { weight(weight = weight, fill = fill) }
        } else if (rowScope != null) {
            with(rowScope) { weight(weight = weight, fill = fill) }
        } else {
            error("ColumnOrRowScope has not been set")
        }
    )

    @Stable
    fun Modifier.fillParentWidth(weight: Float = 1f): Modifier = then(
        if (columnScope != null) {
            with(columnScope) { fillMaxWidth(fraction = weight) }
        } else if (rowScope != null) {
            with(rowScope) { weight(weight = weight, fill = true) }
        } else {
            error("ColumnOrRowScope has not been set")
        }
    )

    @Stable
    fun Modifier.fillParentHeight(weight: Float = 1f): Modifier = then(
        if (columnScope != null) {
            with(columnScope) { weight(weight = weight, fill = true) }
        } else if (rowScope != null) {
            with(rowScope) { fillMaxHeight(fraction = weight) }
        } else {
            error("ColumnOrRowScope has not been set")
        }
    )
}
