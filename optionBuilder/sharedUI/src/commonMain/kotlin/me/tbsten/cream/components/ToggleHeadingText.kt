package me.tbsten.cream.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ToggleHeadingText(
    heading: String,
    iconRes: DrawableResource,
    iconColor: Color = MaterialTheme.colorScheme.primary,
    isDefaultOpen: Boolean = false,
    modifier: Modifier = Modifier,
    placeholder: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    var isOpen by remember { mutableStateOf(isDefaultOpen) }

    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier =
                Modifier
                    .clickable { isOpen = !isOpen }
                    .fillMaxWidth(),
        ) {
            HeadingText(
                heading = heading,
                iconRes = iconRes,
                iconColor = iconColor,
            )
        }

        AnimatedContent(
            targetState = isOpen,
            transitionSpec = {
                (fadeIn() + expandVertically()) togetherWith
                    (fadeOut() + shrinkVertically()) using
                    SizeTransform(clip = false)
            },
        ) { visibleContent ->
            if (visibleContent) {
                content()
            } else {
                placeholder?.invoke()
            }
        }
    }
}
