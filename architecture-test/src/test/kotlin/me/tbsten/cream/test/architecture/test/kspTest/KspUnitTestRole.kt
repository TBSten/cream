package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.cream.test.architecture.KSP_TEST
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.kspUnitTestRole() =
    "KspUnitTest".role {
        title = "横断・単体テスト"
        summary = "feature をまたぐ診断・オプションのテストと、options / core/common の単体テスト"
        example("OptionsDiagnosticTest.kt", "不正なオプションの診断")
        example("options/CreamOptionsParsingTest.kt", "オプションのパースの単体テスト")
        example("core/common/CopyFunctionNameTest.kt", "関数の命名の単体テスト")

        layout {
            KSP_TEST {
                "*Test".ktFile()
                "options" / "*Test".ktFile()
                "core" / "common" / "*Test".ktFile()
            }
        }
    }
