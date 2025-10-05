package me.tbsten.cream.components

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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
            modifier = Modifier
                .clickable { isOpen = !isOpen }
                .fillMaxWidth()
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
            }
        ) { visibleContent ->
            if (visibleContent) content()
            else placeholder?.invoke()
        }
    }
}
