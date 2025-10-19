package me.tbsten.cream

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.components.CommonInputCard
import me.tbsten.cream.components.ToggleHeadingText
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.class_names_heading
import me.tbsten.cream.sharedui.generated.resources.class_names_source_sub_heading
import me.tbsten.cream.sharedui.generated.resources.class_names_target_sub_heading
import me.tbsten.cream.sharedui.generated.resources.icon_slab
import me.tbsten.cream.util.SmallColumnMediumRow
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputClassNamesSection(
    sourceClass: OptionBuilderClassDeclarationInfo,
    onSourceClassChange: (OptionBuilderClassDeclarationInfo) -> Unit,
    targetClass: OptionBuilderClassDeclarationInfo,
    onTargetClassChange: (OptionBuilderClassDeclarationInfo) -> Unit,
) {
    ToggleHeadingText(
        heading = stringResource(Res.string.class_names_heading),
        iconRes = Res.drawable.icon_slab,
        isDefaultOpen = false,
        placeholder = {
            Text(text = "${sourceClass.fullName} -> ${targetClass.fullName}")
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            SmallColumnMediumRow(space = 12.dp) {
                ClassInputCard(
                    value = sourceClass,
                    onValueChange = onSourceClassChange,
                    subHeadingRes = Res.string.class_names_source_sub_heading,
                )

                ClassInputCard(
                    value = targetClass,
                    onValueChange = onTargetClassChange,
                    subHeadingRes = Res.string.class_names_target_sub_heading,
                )
            }
        }
    }
}

@Composable
private fun ClassInputCard(
    value: OptionBuilderClassDeclarationInfo,
    onValueChange: (OptionBuilderClassDeclarationInfo) -> Unit,
    subHeadingRes: StringResource,
) {
    CommonInputCard(
        subHeadingRes = subHeadingRes,
        descriptionRes = null,
    ) {
        OutlinedTextField(
            value = value.packageName,
            onValueChange = { onValueChange(value.copy(packageName = it)) },
            label = { Text("package:") },
            placeholder = { Text("com.myapp") },
            singleLine = true,
        )

        OutlinedTextField(
            value = value.underPackageName,
            onValueChange = { onValueChange(value.copy(underPackageName = it)) },
            label = { Text("class name:") },
            placeholder = { Text("MyUiState") },
            singleLine = true,
        )
    }
}
