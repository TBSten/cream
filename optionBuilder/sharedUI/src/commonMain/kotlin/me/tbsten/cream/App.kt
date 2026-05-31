package me.tbsten.cream

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.tbsten.cream.i18n.AppEnvironment
import me.tbsten.cream.i18n.AppLanguage
import me.tbsten.cream.i18n.LanguageSwitcher
import me.tbsten.cream.i18n.systemLanguageCode
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.app_title
import me.tbsten.cream.theme.AppTextStyles
import me.tbsten.cream.theme.AppTheme
import me.tbsten.cream.util.rememberShareableState
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun App() =
    AppTheme {
        // Hoisted above AppEnvironment so it survives the language-keyed recomposition.
        var language by rememberShareableState("lang", AppLanguage.serializer()) {
            AppLanguage.fromLanguageCode(systemLanguageCode())
        }
        var option by rememberShareableState("option", CreamOptions.serializer()) {
            CreamOptions.default
        }
        var sourceClass by rememberShareableState(
            "source",
            OptionBuilderClassDeclarationInfo.serializer(),
        ) {
            OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState")
        }
        var targetClass by rememberShareableState(
            "target",
            OptionBuilderClassDeclarationInfo.serializer(),
        ) {
            OptionBuilderClassDeclarationInfo("com.myapp", "MyUiState.Success")
        }

        AppEnvironment(language = language) {
            SelectionContainer {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                    modifier =
                        Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                ) {
                    AppHeader(
                        language = language,
                        onLanguageChange = { language = it },
                    )

                    InputOptionsSection(
                        option = option,
                        onChange = { option = it },
                    )

                    InputClassNamesSection(
                        sourceClass = sourceClass,
                        onSourceClassChange = { sourceClass = it },
                        targetClass = targetClass,
                        onTargetClassChange = { targetClass = it },
                    )

                    ResultSection(
                        options = option,
                        sourceClass = sourceClass,
                        targetClass = targetClass,
                    )
                }
            }
        }
    }

@Composable
private fun AppHeader(
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(Res.string.app_title),
            style = AppTextStyles.appTitle,
            modifier = Modifier.weight(1f),
        )
        LanguageSwitcher(
            language = language,
            onLanguageChange = onLanguageChange,
        )
    }
}
