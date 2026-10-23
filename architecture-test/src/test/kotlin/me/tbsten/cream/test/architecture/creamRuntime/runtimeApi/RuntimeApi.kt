package me.tbsten.cream.test.architecture.creamRuntime.runtimeApi

import me.tbsten.cream.test.architecture.support.RUNTIME_SOURCE_DIR
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile
import me.tbsten.katachi.konsist.konsist

internal fun DeclarationContainerScope.runtimeApi() =
    "RuntimeApi" {
        title = "Runtime API"
        summary = "A public annotation, or the enum / name-token constants its arguments take, that users write in their own code"
        description =
            """
            cream-runtime is annotation-only: it ships no logic, so a file holds annotation classes,
            enum classes, or `const val` tokens such as the ones in `CopyFunctionNameToken.kt`.
            """.trimIndent()
        example("CopyTo", "`@CopyTo(Target::class)` written on the source class")
        example("CopyFunctionNameToken", "Placeholders usable in `funName` templates")
        layout {
            RUNTIME_SOURCE_DIR / "*".ktFile()
        }
        packageFollowsDirectory()
        "holds no runtime logic (no functions, no classes other than annotations and enums)".konsist {
            functions(includeNested = false, includeLocal = false).mustBeEmpty()
            classes(includeNested = false, includeLocal = false).must { it.hasAnnotationModifier || it.hasEnumModifier }
        }
    }
