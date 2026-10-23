package me.tbsten.cream.test.architecture.creamKspTest.featureTest.invalidUsageTest

import me.tbsten.cream.test.architecture.support.KSP_TEST_DIR
import me.tbsten.cream.test.architecture.support.isInternalFreeSpec
import me.tbsten.cream.test.architecture.support.isNamedAfterFeatureDirectory
import me.tbsten.cream.test.architecture.support.packageFollowsDirectory
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.kotlin.ktFile

internal fun DeclarationContainerScope.invalidUsageTest() =
    "InvalidUsageTest" {
        summary = "不正利用 → エラー (diagnostic)"
        description =
            """
            不正 annotation / 不正 option は exit code を `result.exitCode shouldNotBe KotlinCompilation.ExitCode.OK` で確認し、
            `normalizedCompilerOutput()` を `*.output.md` golden と突き合わせる（`MultipleDiagnosticsTest` も同じ）。

            `normalizedCompilerOutput()` は以下を置換 / 集約してから返す:

            - `java.io.tmpdir` → `<TMPDIR>`
            - `Kotlin-CompilationNNN` → `Kotlin-Compilation<N>`
            - 連続する `\tat ...` および `\t... NN more`（stack-trace フレーム）→ 1 行の `\t<stack trace omitted>`

            stack frame は Gradle / JUnit / KSP / cream 自身の line 変更や `... NN more` の depth count で簡単にずれるため、
            diagnostic snapshot で固定化しているのは「error message 本文」だけ。特定 frame の存在を assert したいなら、snapshot とは別に
            `result.messages shouldContain "..."` を併用する。
            """.trimIndent()
        example("CopyToInvalidUsageTest")
        layout {
            KSP_TEST_DIR / "feature" / "*" / "*InvalidUsageTest".ktFile()
        }
        packageFollowsDirectory()
        isNamedAfterFeatureDirectory()
        isInternalFreeSpec()
    }
