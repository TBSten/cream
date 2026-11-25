@file:OptIn(ExperimentalKatachiApi::class)

package me.tbsten.cream.test.architecture.test.integrationTest

import me.tbsten.cream.test.architecture.INTEGRATION_TEST
import me.tbsten.cream.test.architecture.INTEGRATION_TEST_DATA
import me.tbsten.cream.test.architecture.support.`対になるファイルがある`
import me.tbsten.katachi.ExperimentalKatachiApi
import me.tbsten.katachi.dsl.DeclarationContainerScope
import me.tbsten.katachi.dsl.FileConstraintScope
import me.tbsten.katachi.dsl.kotlin.ktFile

fun DeclarationContainerScope.integrationTestRole() =
    "IntegrationTest".role {
        title = "結合テスト"
        summary = "生成された関数を呼んで結果を確かめるテスト（commonTest）"
        example("copyTo/BasicTest.kt", "Basic.kt の生成関数のテスト")

        layout {
            INTEGRATION_TEST / "*" / "**" / "*Test".ktFile()

            `対になるデータファイルがある`()
        }
    }

private fun FileConstraintScope.`対になるデータファイルがある`() =
    `対になるファイルがある`(
        name = "対になるデータファイルがある",
        howToFix = "commonMain に同じパスの <Name>.kt を置くか、名前を揃える",
    ) { file ->
        "$INTEGRATION_TEST_DATA/" + file.removePrefix("$INTEGRATION_TEST/").removeSuffix("Test.kt") + ".kt"
    }
