package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSName
import java.io.BufferedWriter

internal fun CodeGenerator.createNewKotlinFile(
    dependencies: Dependencies,
    packageName: KSName,
    fileName: String,
    block: (BufferedWriter) -> Unit,
) = createNewFile(
    dependencies = dependencies,
    packageName = packageName.asString(),
    fileName = fileName,
).bufferedWriter()
    .use {
        it.appendLine("package ${packageName.asString()}")
        it.appendLine()

        block(it)
    }
