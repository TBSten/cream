package me.tbsten.cream.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.icon_info
import me.tbsten.cream.theme.AppTextStyles

@Composable
fun Toggle(
    heading: String,
    isDefaultOpen: Boolean = false,
    info: String? = null,
    modifier: Modifier = Modifier,
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
            Text(
                text = heading,
                style = AppTextStyles.heading,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
            )

            info?.let {
                CommonIcon(
                    icon = Res.drawable.icon_info,
                    contentDescription = info,
                )
            }
        }

        AnimatedVisibility(
            visible = isOpen,
        ) {
            content()
        }
    }
}
