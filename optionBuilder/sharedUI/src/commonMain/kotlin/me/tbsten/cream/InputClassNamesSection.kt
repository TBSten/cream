package me.tbsten.cream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.components.CommonInputCard
import me.tbsten.cream.components.Toggle
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.class_names_heading
import me.tbsten.cream.sharedui.generated.resources.class_names_source_sub_heading
import me.tbsten.cream.sharedui.generated.resources.class_names_target_sub_heading
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun InputClassNamesSection(
    sourceClass: OptionBuilderClassDeclarationInfo,
    onSourceClassChange: (OptionBuilderClassDeclarationInfo) -> Unit,
    targetClass: OptionBuilderClassDeclarationInfo,
    onTargetClassChange: (OptionBuilderClassDeclarationInfo) -> Unit,
) {
    Toggle(
        heading = stringResource(Res.string.class_names_heading),
        isDefaultOpen = false,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
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