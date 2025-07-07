package me.tbsten.cream.ksp.options

import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.tbsten.cream.ksp.util.fullName
import me.tbsten.cream.ksp.util.underPackageName

@Suppress("EnumEntryName", "RemoveRedundantBackticks")
internal enum class CopyFunNamingStrategy(val funName: (source: KSClassDeclaration, target: KSClassDeclaration) -> String) {
    `under-package`({ _, target ->
        target.fullName.replace(target.packageName.asString() + ".", "")
    }),
    `diff`(
        { source, target ->
            val sourceName = source.fullName
            val targetName = target.fullName
            var i = 0
            val minLength = minOf(sourceName.length, targetName.length)

            // 共通部分をスキップ
            while (i < minLength && sourceName[i] == targetName[i]) {
                i++
            }

            // second だけにある差分
            targetName.substring(i)
        }
    ),
    `simple-name`(
        { _, target -> target.simpleName.asString() }
    ),
    `full-name`(
        { _, target -> target.fullName }
    ),
    `inner-name`(
        { _, target ->
            target.underPackageName
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
