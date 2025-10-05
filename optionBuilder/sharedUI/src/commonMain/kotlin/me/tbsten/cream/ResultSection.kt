package me.tbsten.cream

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.transform.copyFunctionName
import me.tbsten.cream.sharedui.generated.resources.Res
import me.tbsten.cream.sharedui.generated.resources.result_heading
import me.tbsten.cream.theme.AppTextStyles
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CopyFunctionName(
                options = options,
                sourceClass = sourceClass,
                targetClass = targetClass,
                modifier = Modifier.weight(2f).padding(top = 40.dp),
            )

            GradleSetting(
                options = options,
                modifier = Modifier.weight(1f),
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
            fontSize = 48.sp,
            lineHeight = 48.sp,
        )
    }
}

@Composable
private fun GradleSetting(
    options: CreamOptions,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            """
            // build.gradle.kts
            plugins {
                id("com.google.devtools.ksp") version "<ksp-version>"
            }

            dependencies {
                implementation("me.tbsten.cream:cream-runtime:<cream-version>")
                ksp("me.tbsten.cream:cream-ksp:<cream-version>")
            }

            ksp {
                arg("cream.copyFunNamePrefix", "${options.copyFunNamePrefix}")
                arg("cream.copyFunNamingStrategy", "${options.copyFunNamingStrategy}")
                arg("cream.escapeDot", "${options.escapeDot}")
                arg("cream.notCopyToObject", ${options.notCopyToObject})
            }
        """.trimIndent()
        )
    }
}
