package me.tbsten.cream.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.tbsten.cream.theme.AppTextStyles
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun HeadingText(
    heading: String,
    iconRes: DrawableResource,
    iconColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CommonIcon(
            icon = iconRes,
            contentDescription = heading,
            color = iconColor,
        )

        Text(
            text = heading,
            style = AppTextStyles.heading,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )
    }
}