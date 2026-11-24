package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.cream.test.architecture.KSP_TEST
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.konsistTestRole() =
    "KonsistTest".role {
        title = "レイヤのテスト"
        summary = "Konsist で feature / core / util の依存方向を強制するテスト"
        example("core/ArchTest.kt", "core のレイヤ規則")

        layout {
            KSP_TEST {
                "core" / "ArchTest".ktFile()
                "feature" / "ArchTest".ktFile()
            }
        }
    }
