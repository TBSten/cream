package me.tbsten.cream.ksp.testing.poet

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.generator.generator

/**
 * スナップショット 1 件の入力 = コンパイルする top-level 宣言の集合。feature 非依存（@CopyTo / @CombineTo …
 * どの feature の scenario も「コンパイルする宣言の集合」として表現できる）。
 */
internal data class SnapshotScenario(
    val topLevelDeclarations: List<TypeSpec>,
) {
    constructor(vararg declarations: TypeSpec) : this(declarations.toList())
}

/** [SnapshotScenario] を 1 ファイルの [FileSpec] にする（`runCompileSnapshotTest` に渡す入力）。 */
internal fun SnapshotScenario.toFileSpec(fileName: String = "Input"): FileSpec =
    FileSpec
        .builder(GENERATED_PACKAGE, fileName)
        .apply { topLevelDeclarations.forEach { addType(it) } }
        .build()

/** curated な (label -> scenario) 群を [Generator] に。arb は representative からの一様抽選。 */
internal fun Generator.Companion.snapshotScenarios(vararg cases: Pair<String, SnapshotScenario>): Generator<SnapshotScenario> =
    generator {
        cases.forEach { (label, scenario) -> label case scenario }
        Arb.of(cases.map { it.second })
    }
