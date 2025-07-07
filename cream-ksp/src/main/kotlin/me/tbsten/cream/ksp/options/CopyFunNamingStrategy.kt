package me.tbsten.cream.ksp.options

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.name

@Suppress("EnumEntryName")
internal enum class CopyFunNamingStrategy(val funName: (source: KSClassDeclaration, target: KSClassDeclaration) -> String) {
    `under-package`({ _, target ->
        target.fullName.replace(target.packageName.asString() + ".", "")
    }),
    `diff-parent`(
        { source, target -> target.fullName.replace(source.fullName, "") }
    ),
    `simple-name`(
        { _, target -> target.simpleName.asString() }
    ),
    `full-name`(
        { source, target -> target.fullName }
    ),
    `inner-name`(
        { _, target ->
            target.name
                .split(".")
                .let {
                    if (it.size <= 1) {
                        it
                    } else {
                        it.subList(minOf(it.size, 1), it.size)
                    }
                }
                .joinToString(".")
        }
    )
    ;

    companion object {
        val default = `under-package`
    }
}
