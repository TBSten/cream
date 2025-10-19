package me.tbsten.cream.components

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonIcon(
    icon: DrawableResource,
    contentDescription: String,
    color: Color,
) {
    Icon(
        painter = painterResource(icon),
        contentDescription = contentDescription,
        tint = color,
        modifier =
            Modifier
                .hoverable(remember { MutableInteractionSource() })
                .pointerHoverIcon(PointerIcon.Crosshair)
                .size(24.dp),
    )
}
