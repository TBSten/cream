package me.tbsten.cream.ksp.options

import me.tbsten.cream.InternalCreamApi

@Suppress("EnumEntryName", "RemoveRedundantBackticks")
@InternalCreamApi
public enum class CopyFunNamingStrategy(public val funName: (source: ClassDeclarationInfo, target: ClassDeclarationInfo) -> String) {
    `under-package`({ _, target ->
        target.fullName.replace(target.packageName + ".", "")
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
        { _, target -> target.simpleName }
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

    public companion object {
        public val default: CopyFunNamingStrategy = `under-package`
    }
}

@InternalCreamApi
public interface ClassDeclarationInfo {
    public val packageName: String
    public val underPackageName: String
    public val simpleName: String

    public val fullName: String
}
