package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName
import me.tbsten.cream.ksp.util.visibilityStr
import java.io.BufferedWriter


internal fun BufferedWriter.appendCopyToObjectFunction(
    source: KSClassDeclaration,
    targetObject: KSClassDeclaration,
    options: CreamOptions,
) {
    appendKDoc(source, targetObject)
    appendLine(
        "${targetObject.visibilityStr} fun ${source.fullName}.${
            copyFunctionName(
                source,
                targetObject,
                options,
            )
        }() = ${targetObject.fullName}"
    )
}

private fun BufferedWriter.appendKDoc(
    source: KSClassDeclaration,
    target: KSClassDeclaration,
) {
    appendLine("/**")
    appendLine(" * (auto generated)")
    appendLine(" * [${source.underPackageName}] -> [${target.underPackageName}] copy function.")
    appendLine(" * ")
    appendLine(" * @see ${source.underPackageName}")
    appendLine(" * @see ${target.underPackageName}")
    appendLine(" */")
}
