package me.tbsten.cream.test.architecture.library.ksp

import me.tbsten.cream.test.architecture.KSP_MAIN
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.kspOptionRole() =
    "KspOption".role {
        title = "KSP オプション"
        summary = "KSP の引数（cream.xxx）を表すモデルとそのパース"
        example("CreamOptions.kt", "全オプションのデータクラスとパース")

        layout {
            KSP_MAIN / "options" / "*".ktFile()
        }
    }
