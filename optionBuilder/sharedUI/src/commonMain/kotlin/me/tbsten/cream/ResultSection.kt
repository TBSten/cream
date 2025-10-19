package me.tbsten.cream

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxLanguage
import generateAnnotatedString
import kotlinx.serialization.builtins.serializer
import me.tbsten.cream.components.HeadingText
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.transform.copyFunctionName
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.gradle_comment_select_ksp_version
import me.tbsten.cream.sharedui.generated.resources.gradle_setting_heading
import me.tbsten.cream.sharedui.generated.resources.icon_circle_check
import me.tbsten.cream.sharedui.generated.resources.icon_code_block
import me.tbsten.cream.sharedui.generated.resources.result_heading
import me.tbsten.cream.sharedui.generated.resources.show_all
import me.tbsten.cream.theme.MainGradient
import me.tbsten.cream.theme.SubGradient
import me.tbsten.cream.util.MediumColumnLargeRow
import me.tbsten.cream.util.adaptive
import me.tbsten.cream.util.rememberShareableState
import org.jetbrains.compose.resources.stringResource

@Composable
fun ResultSection(
    options: CreamOptions,
    sourceClass: ClassDeclarationInfo,
    targetClass: ClassDeclarationInfo,
) {
    Column {
        MediumColumnLargeRow(space = 20.dp) {
            CopyFunctionName(
                options = options,
                sourceClass = sourceClass,
                targetClass = targetClass,
                modifier = Modifier.fillParentWidth(2f),
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
        HeadingText(
            heading = stringResource(Res.string.result_heading),
            iconRes = Res.drawable.icon_circle_check,
        )

        val smallFontSize = adaptive(small = 24.sp, medium = 40.sp)
        val largeFontSize = adaptive(small = 28.sp, medium = 48.sp)

        Text(
            buildAnnotatedString {
                appendLine("${sourceClass.underPackageName}.")
                withStyle(
                    SpanStyle(
                        brush = MainGradient,
                        fontWeight = FontWeight.Bold,
                        fontSize = largeFontSize,
                    ),
                ) {
                    append(copyFunName.prefix)
                }
                withStyle(
                    SpanStyle(
                        brush = SubGradient,
                        fontWeight = FontWeight.Bold,
                        fontSize = largeFontSize,
                    ),
                ) {
                    append(copyFunName.targetName)
                }
                append("(...)")
            },
            color = Color.Gray,
            fontSize = smallFontSize,
            lineHeight = smallFontSize,
            modifier = Modifier.padding(vertical = 48.dp),
        )
    }
}

private fun gradleSettingCode(
    isFull: Boolean,
    options: CreamOptions,
    kspVersionComment: String,
): String {
    val kspVersion = BuildKonfig.kspVersion
    val creamVersion = BuildKonfig.creamVersion

    return if (isFull) {
        """
        // build.gradle.kts
        plugins {
            // $kspVersionComment
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
    var isFull by rememberShareableState(
        "full-code",
        Boolean.serializer(),
    ) { false }

    Column(modifier = modifier) {
        HeadingText(
            heading = stringResource(Res.string.gradle_setting_heading),
            iconRes = Res.drawable.icon_code_block,
        )

        val kspVersionComment = stringResource(Res.string.gradle_comment_select_ksp_version)
        val highlights =
            remember(isFull, kspVersionComment) {
                mutableStateOf(
                    Highlights
                        .Builder(
                            language = SyntaxLanguage.KOTLIN,
                            code = gradleSettingCode(isFull, options, kspVersionComment),
                        ).build(),
                )
            }

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(4.dp),
        ) {
            val cardInnerPadding = 12.dp

            Column(
                modifier =
                    Modifier
                        .animateContentSize()
                        .padding(vertical = cardInnerPadding),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier =
                        Modifier
                            .padding(horizontal = 12.dp),
                ) {
                    Switch(
                        checked = isFull,
                        onCheckedChange = { isFull = it },
                    )
                    Text(text = stringResource(Res.string.show_all))
                }

                HorizontalDivider()

                CodeTextView(
                    highlights = highlights.value,
                    modifier =
                        Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 12.dp),
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
        textState =
            highlights
                .getHighlights()
                .generateAnnotatedString(highlights.getCode())
    }

    Text(
        modifier = modifier,
        text = textState,
    )
}
