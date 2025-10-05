package me.tbsten.cream.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.theme.AppTextStyles
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource


@Composable
internal fun CommonInputCard(
    subHeadingRes: StringResource,
    descriptionRes: StringResource?,
    modifier: Modifier = Modifier,
    input: @Composable () -> Unit,
) {
    CommonCard(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier,
    ) {
        Text(
            text = stringResource(subHeadingRes),
            style = AppTextStyles.subHeading,
        )

        descriptionRes?.let {
            Text(
                text = stringResource(descriptionRes).trim(),
                style = AppTextStyles.description,
            )
        }

        input()
    }
}

