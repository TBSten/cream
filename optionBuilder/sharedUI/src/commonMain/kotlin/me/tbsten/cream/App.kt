package me.tbsten.cream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.theme.AppTheme
import me.tbsten.cream.util.rememberSavableSessionState
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun App() = AppTheme {
    var option by rememberSavableSessionState("option", CreamOptions.serializer()) {
        CreamOptions.default
    }
    var sourceClass by rememberSavableSessionState(
        "source",
        OptionBuilderClassDeclarationInfo.serializer(),
    ) {
        OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState")
    }
    var targetClass by rememberSavableSessionState(
        "target",
        OptionBuilderClassDeclarationInfo.serializer(),
    ) {
        OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState.Success")
    }

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
