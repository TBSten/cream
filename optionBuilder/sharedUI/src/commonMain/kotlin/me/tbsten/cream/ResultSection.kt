package me.tbsten.cream

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxLanguage
import generateAnnotatedString
import kotlinx.serialization.builtins.serializer
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.transform.copyFunctionName
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.result_heading
import me.tbsten.cream.theme.AppTextStyles
import me.tbsten.cream.util.MediumColumnLargeRow
import me.tbsten.cream.util.adaptive
import me.tbsten.cream.util.rememberSavableSessionState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultSection(
    options: CreamOptions,
    sourceClass: ClassDeclarationInfo,
    targetClass: ClassDeclarationInfo,
) {
    Column {
        Text(
            text = stringResource(Res.string.result_heading),
            style = AppTextStyles.heading,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp),
        )

        MediumColumnLargeRow(space = 20.dp) {
            CopyFunctionName(
                options = options,
                sourceClass = sourceClass,
                targetClass = targetClass,
                modifier = Modifier.fillParentWidth(2f).padding(top = 40.dp, bottom = 20.dp),
            )

            GradleSetting(
                options = options,
                modifier = Modifier.fillParentWidth(1f),
            )
        }
    }
}


@Composable
private fun CopyFunctionName(
    options: CreamOptions,
    sourceClass: ClassDeclarationInfo,
    targetClass: ClassDeclarationInfo,
    modifier: Modifier = Modifier,
) {
    val copyFunName = copyFunctionName(source = sourceClass, target = targetClass, options = options)

    Column(modifier = modifier) {
        val fontSize = adaptive(small = 28.sp, medium = 48.sp)

        Text(
            buildAnnotatedString {
                appendLine("${sourceClass.underPackageName}.")
                withStyle(SpanStyle(brush = Brush.linearGradient(0f to Color.Yellow, 1f to Color.Green))) {
                    append(copyFunName.prefix)
                }
                withStyle(SpanStyle(brush = Brush.linearGradient(0f to Color.Red, 1f to Color.Blue))) {
                    append(copyFunName.targetName)
                }
                append("(...)")
            },
            color = Color.Gray,
            fontSize = fontSize,
            lineHeight = fontSize,
        )
    }
}

private fun gradleSettingCode(isFull: Boolean, options: CreamOptions): String {
    val kspVersion = BuildKonfig.kspVersion
    val creamVersion = BuildKonfig.creamVersion

    return if (isFull) {
        """
        // build.gradle.kts
        plugins {
            // あなたの プロジェクトの kotlin バージョンに合った 
            // KSP バージョンを選択してください
            // https://github.com/google/ksp/releases
            id("com.google.devtools.ksp") version "$kspVersion"
        }

        dependencies {
            implementation("me.tbsten.cream:cream-runtime:$creamVersion")
            ksp("me.tbsten.cream:cream-ksp:$creamVersion")
        }

        ksp {
            arg("cream.copyFunNamePrefix", "${options.copyFunNamePrefix}")
            arg("cream.copyFunNamingStrategy", "${options.copyFunNamingStrategy}")
            arg("cream.escapeDot", "${options.escapeDot}")
            arg("cream.notCopyToObject", ${options.notCopyToObject})
        }
        """.trimIndent()
    } else {
        """
        // build.gradle.kts
        ksp {
            arg("cream.copyFunNamePrefix", "${options.copyFunNamePrefix}")
            arg("cream.copyFunNamingStrategy", "${options.copyFunNamingStrategy}")
            arg("cream.escapeDot", "${options.escapeDot}")
            arg("cream.notCopyToObject", ${options.notCopyToObject})
        }
        """.trimIndent()
    }
}

@Composable
private fun GradleSetting(
    options: CreamOptions,
    modifier: Modifier = Modifier,
) {
    var isFull by rememberSavableSessionState(
        "",
        Boolean.serializer(),
    ) { false }

    Column(modifier = modifier) {
        val highlights = remember(isFull) {
            mutableStateOf(
                Highlights
                    .Builder(
                        language = SyntaxLanguage.KOTLIN,
                        code = gradleSettingCode(isFull, options),
                    )
                    .build()
            )
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            val cardInnerPadding = 12.dp

            Column(
                modifier = Modifier
                    .animateContentSize()
                    .padding(vertical = cardInnerPadding)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    Switch(
                        checked = isFull,
                        onCheckedChange = { isFull = it },
                    )
                    Text(text = "全て表示")
                }

                HorizontalDivider()

                CodeTextView(
                    highlights = highlights.value,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun CodeTextView(
    highlights: Highlights,
    modifier: Modifier = Modifier,
) {
    var textState by remember {
        mutableStateOf(AnnotatedString(highlights.getCode()))
    }

    LaunchedEffect(highlights) {
        textState = highlights
            .getHighlights()
            .generateAnnotatedString(highlights.getCode())
    }

    Text(
        modifier = modifier,
        text = textState
    )
}
