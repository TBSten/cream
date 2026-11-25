@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.test.integrationTest

import me.tbsten.cream.test.architecture.INTEGRATION_TEST
import me.tbsten.cream.test.architecture.INTEGRATION_TEST_DATA
import me.tbsten.cream.test.architecture.support.`対になるファイルがある`
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.FileConstraintScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.integrationTestDataRole() =
    "IntegrationTestData".role {
        title = "結合テストのデータ"
        summary = "アノテーションを付けたテスト用クラス（commonMain）。KSP がここからコードを生成する"
        description =
            """
            <feature>/<Name>.kt に置き、commonTest の同じパスに <Name>Test.kt を置く。
            <feature>/ のサブディレクトリ（copyMapping/lib/ など）には、直下のデータが使う補助のモデルを置く。補助のモデルには対のテストは要らない。
            """.trimIndent()
        example("copyTo/Basic.kt", "@CopyTo の基本ケース")

        layout {
            INTEGRATION_TEST_DATA / "*" / "**" / "*".ktFile()

            `対になるテストファイルがある`()
        }
    }

private fun FileConstraintScope.`対になるテストファイルがある`() =
    `対になるファイルがある`(
        name = "対になる *Test.kt がある",
        howToFix = "commonTest に同じパスの <Name>Test.kt を置くか、名前を揃える",
    ) { file ->
        val relative = file.removePrefix("$INTEGRATION_TEST_DATA/")
        // <feature>/ のサブディレクトリにある補助のモデルは対のテストを持たない
        if (relative.count { it == '/' } == 1) "$INTEGRATION_TEST/" + relative.removeSuffix(".kt") + "Test.kt" else null
    }
