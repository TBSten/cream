package me.tbsten.cream.test.architecture.test.kspTest

import me.tbsten.cream.test.architecture.KSP_TEST_RESOURCES
import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.snapshotGoldenRole() =
    "SnapshotGolden".role {
        documented = false
        title = "snapshot の golden"
        summary = "snapshot テストが比較する golden ファイル（テストクラスごとのディレクトリ）"
        example("CopyToSnapshotTest/Generics/basic.md", "1 scenario の生成結果")

        layout {
            KSP_TEST_RESOURCES / "snapshots" / "*" / "**" / "*.md".file()
        }
    }
