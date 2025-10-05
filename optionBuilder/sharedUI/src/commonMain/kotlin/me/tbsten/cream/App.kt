package me.tbsten.cream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun App() = AppTheme {
    var option by remember { mutableStateOf(CreamOptions.default) }
    var sourceClass by remember { mutableStateOf(OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState")) }
    var targetClass by remember { mutableStateOf(OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState.Success")) }

    SelectionContainer {
        Column(
            verticalArrangement = Arrangement.spacedBy(40.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            InputOptionsSection(
                option = option,
                onChange = { option = it },
            )

            InputClassNamesSection(
                sourceClass = sourceClass,
                onSourceClassChange = { sourceClass = it },
                targetClass = targetClass,
                onTargetClassChange = { targetClass = it }
            )

            ResultSection(
                options = option,
                sourceClass = sourceClass,
                targetClass = targetClass,
            )
        }
    }
}
