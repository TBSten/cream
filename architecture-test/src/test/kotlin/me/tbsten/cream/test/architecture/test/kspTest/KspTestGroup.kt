package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.kspTestGroup() =
    "KspTest".group {
        summary = "cream-ksp の JVM テスト（kctfork / snapshot / Konsist）"

        featureTestRole()
        kspUnitTestRole()
        konsistTestRole()
        testingSupportRole()
        snapshotGoldenRole()
    }
