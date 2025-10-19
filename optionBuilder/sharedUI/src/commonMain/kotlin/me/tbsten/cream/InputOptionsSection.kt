package me.tbsten.cream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.components.CommonIconButton
import me.tbsten.cream.components.CommonInputCard
import me.tbsten.cream.components.ToggleHeadingText
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.icon_build
import me.tbsten.cream.sharedui.generated.resources.icon_reset
import me.tbsten.cream.sharedui.generated.resources.options_copy_fun_name_prefix_description
import me.tbsten.cream.sharedui.generated.resources.options_copy_fun_name_prefix_heading
import me.tbsten.cream.sharedui.generated.resources.options_copy_fun_naming_strategy_description
import me.tbsten.cream.sharedui.generated.resources.options_copy_fun_naming_strategy_heading
import me.tbsten.cream.sharedui.generated.resources.options_escape_dot_description
import me.tbsten.cream.sharedui.generated.resources.options_escape_dot_heading
import me.tbsten.cream.sharedui.generated.resources.options_heading
import me.tbsten.cream.sharedui.generated.resources.reset
import me.tbsten.cream.util.SmallColumnMediumRow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.enums.enumEntries

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputOptionsSection(
    option: CreamOptions,
    onChange: (CreamOptions) -> Unit,
) {
    ToggleHeadingText(
        heading = stringResource(Res.string.options_heading),
        iconRes = Res.drawable.icon_build,
        isDefaultOpen = true,
    ) {
        Column {
            Row(modifier = Modifier.padding(vertical = 20.dp)) {
                SmallColumnMediumRow(space = 20.dp) {
                    InputTextOptionCard(
                        value = option.copyFunNamePrefix,
                        defaultValue = CreamOptions.default.copyFunNamePrefix,
                        onChange = { onChange(option.copy(copyFunNamePrefix = it)) },
                        subHeadingRes = Res.string.options_copy_fun_name_prefix_heading,
                        descriptionRes = Res.string.options_copy_fun_name_prefix_description,
                        modifier = Modifier.fillParentWidth(1f),
                    )
                    SelectOptionCard(
                        value = option.copyFunNamingStrategy,
                        defaultValue = CreamOptions.default.copyFunNamingStrategy,
                        onChange = { onChange(option.copy(copyFunNamingStrategy = it)) },
                        subHeadingRes = Res.string.options_copy_fun_naming_strategy_heading,
                        descriptionRes = Res.string.options_copy_fun_naming_strategy_description,
                        modifier = Modifier.fillParentWidth(1f),
                    )
                    SelectOptionCard(
                        value = option.escapeDot,
                        defaultValue = CreamOptions.default.escapeDot,
                        onChange = { onChange(option.copy(escapeDot = it)) },
                        subHeadingRes = Res.string.options_escape_dot_heading,
                        descriptionRes = Res.string.options_escape_dot_description,
                        modifier = Modifier.fillParentWidth(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun InputTextOptionCard(
    value: String,
    defaultValue: String,
    subHeadingRes: StringResource,
    descriptionRes: StringResource,
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CommonInputCard(
        subHeadingRes = subHeadingRes,
        descriptionRes = descriptionRes,
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = value,
                onValueChange = onChange,
                placeholder = { Text(defaultValue) },
                singleLine = true,
                modifier = Modifier.weight(1f),
            )

            CommonIconButton(
                icon = Res.drawable.icon_reset,
                contentDescription = stringResource(Res.string.reset),
                onClick = { onChange(defaultValue) },
            )
        }
    }
}

@Composable
private fun <T> SelectOptionCard(
    value: T,
    choices: List<T>,
    defaultValue: T,
    subHeadingRes: StringResource,
    descriptionRes: StringResource,
    label: (T) -> String,
    onChange: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    CommonInputCard(
        subHeadingRes = subHeadingRes,
        descriptionRes = descriptionRes,
        modifier = modifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.weight(1f),
            ) {
                OutlinedButton(
                    onClick = { isMenuOpen = !isMenuOpen },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = label(value))
                }
                DisableSelection {
                    DropdownMenu(expanded = isMenuOpen, onDismissRequest = { isMenuOpen = false }) {
                        choices.forEach { choice ->
                            DropdownMenuItem(
                                text = { Text(label(choice)) },
                                onClick = {
                                    onChange(choice)
                                    isMenuOpen = false
                                },
                            )
                        }
                    }
                }
            }

            CommonIconButton(
                icon = Res.drawable.icon_reset,
                contentDescription = stringResource(Res.string.reset),
                onClick = { onChange(defaultValue) },
            )
        }
    }
}

@Composable
private inline fun <reified E : Enum<E>> SelectOptionCard(
    value: E,
    defaultValue: E,
    subHeadingRes: StringResource,
    descriptionRes: StringResource,
    noinline onChange: (E) -> Unit,
    modifier: Modifier = Modifier,
) = SelectOptionCard(
    value = value,
    choices = enumEntries<E>(),
    defaultValue = defaultValue,
    subHeadingRes = subHeadingRes,
    descriptionRes = descriptionRes,
    label = { it.name },
    onChange = onChange,
    modifier = modifier,
)
