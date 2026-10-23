package me.tbsten.cream.test.architecture.creamKspTest.featureTest

import me.tbsten.cream.test.architecture.creamKspTest.featureTest.basicUsageTest.basicUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.edgeUsageTest.edgeUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.invalidUsageTest.invalidUsageTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.propertyTest.propertyTest
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.scenario.scenario
import me.tbsten.cream.test.architecture.creamKspTest.featureTest.snapshotTest.snapshotTest
import me.tbsten.katachi.dsl.DeclarationContainerScope

internal fun DeclarationContainerScope.featureTest() =
    "featureTest".group {
        title = "Feature tests"
        summary = "Every `feature/<name>/` carries the same five kinds of test, plus the curated scenarios its snapshot test uses"
        description =
            """
            - **feature 5 種**: 各 feature は `BasicUsage` / `InvalidUsage` / `EdgeUsage` / `Property` / `Snapshot` の 5 ファイルを持つ
              （正常系 / 不正系 / エッジ / PBT / snapshot）。テストを足すときは該当ファイルに追加し、まだ `xtest` スタブのファイルは
              中身を実装し直す形で置き換える。
            - **現状 (#127)**: 全 feature の `<Feat>SnapshotTest`（`scenario/` 付き）は実装済み。`BasicUsage` / `InvalidUsage` / `EdgeUsage` /
              `Property` は一部を除きまだ `xtest` の空スタブ（`// TODO(#127): reimplement ...`）で、順次実装し直す。
            """.trimIndent()

        basicUsageTest()
        invalidUsageTest()
        edgeUsageTest()
        propertyTest()
        snapshotTest()
        scenario()
    }
