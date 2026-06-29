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
 *
 * 宣言は [files]（= パッケージ単位の [ScenarioFile]）でまとめる。大半の scenario は単一パッケージ
 * （[GENERATED_PACKAGE]）なので `vararg` / `List<TypeSpec>` の便宜コンストラクタで足りる。コピー元/コピー先と
 * holder が別パッケージといった cross-package scenario は [ScenarioFile] を複数渡して表現する。
 */
internal data class SnapshotScenario(
    val files: List<ScenarioFile>,
)

/**
 * 単一パッケージ（[GENERATED_PACKAGE]）の宣言集合から [SnapshotScenario] を作る便宜ファクトリ。大半の
 * scenario 用。`List<ScenarioFile>` を取る本来のコンストラクタと JVM シグネチャが衝突するため、コンストラクタ
 * ではなく同名のトップレベル関数にしている（呼び出し側は `SnapshotScenario(...)` のまま）。
 */
internal fun SnapshotScenario(declarations: List<TypeSpec>): SnapshotScenario = SnapshotScenario(listOf(ScenarioFile(GENERATED_PACKAGE, declarations)))

internal fun SnapshotScenario(vararg declarations: TypeSpec): SnapshotScenario = SnapshotScenario(declarations.toList())

/** [SnapshotScenario] を構成する 1 ファイル分 = ある [packageName] に置く top-level 宣言の集合。 */
internal data class ScenarioFile(
    val packageName: String,
    val topLevelDeclarations: List<TypeSpec>,
) {
    constructor(packageName: String, vararg declarations: TypeSpec) : this(packageName, declarations.toList())
}

/**
 * [SnapshotScenario] を [FileSpec] 群にする（`runCompileSnapshotTest(inputs = ...)` に渡す入力）。
 * [ScenarioFile] ごとに 1 ファイル。ファイル名（= snapshot の `Input:` facet 名）は、そのファイルの
 * 先頭宣言の完全修飾名（例 `com.example.myapp.Source`）にして、何が入力かが facet 見出しから分かるようにする。
 */
internal fun SnapshotScenario.toFileSpecs(): List<FileSpec> =
    files.map { file ->
        val fileName =
            file.topLevelDeclarations
                .firstOrNull()
                ?.name
                ?.let { "${file.packageName}.$it" }
                ?: file.packageName
        FileSpec
            .builder(file.packageName, fileName)
            .apply { file.topLevelDeclarations.forEach { addType(it) } }
            .build()
    }

/** curated な (label -> scenario) 群を [Generator] に。arb は representative からの一様抽選。 */
internal fun Generator.Companion.snapshotScenarios(vararg cases: Pair<String, SnapshotScenario>): Generator<SnapshotScenario> =
    generator {
        cases.forEach { (label, scenario) -> label case scenario }
        Arb.of(cases.map { it.second })
    }
