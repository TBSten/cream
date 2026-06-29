package me.tbsten.cream.ksp.testing.poet

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.of
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.clazz.GENERATED_PACKAGE
import me.tbsten.cream.ksp.testing.generator.generator

/**
 * スナップショット 1 件の入力 = コンパイルする [FileSpec] の集合。feature 非依存（@CopyTo / @CombineTo …
 * どの feature の scenario も「コンパイルする宣言の集合」として表現できる）。[files] はそのまま
 * `runCompileSnapshotTest(inputs = ...)` に渡せる。
 *
 * 大半の scenario は単一パッケージ（[GENERATED_PACKAGE]）なので `vararg` / `List<TypeSpec>` の便宜ファクトリで
 * 足りる。コピー元/コピー先と holder が別パッケージといった cross-package scenario は [inputFileSpec] を
 * 複数組み合わせて渡す。
 */
internal data class SnapshotScenario(
    val files: List<FileSpec>,
)

/**
 * 単一パッケージ（[GENERATED_PACKAGE]）の宣言集合から [SnapshotScenario] を作る便宜ファクトリ。大半の
 * scenario 用。`List<FileSpec>` を取る本来のコンストラクタと JVM シグネチャが衝突するため、コンストラクタ
 * ではなく同名のトップレベル関数にしている（呼び出し側は `SnapshotScenario(...)` のまま）。
 */
internal fun SnapshotScenario(declarations: List<TypeSpec>): SnapshotScenario = SnapshotScenario(listOf(inputFileSpec(GENERATED_PACKAGE, declarations)))

internal fun SnapshotScenario(vararg declarations: TypeSpec): SnapshotScenario = SnapshotScenario(declarations.toList())

/**
 * ある [packageName] の宣言集合を、snapshot 入力用の [FileSpec] にする。ファイル名（= snapshot の `Input:`
 * facet 名）は先頭宣言の完全修飾名（例 `com.example.myapp.Source`）にして、何が入力かが facet 見出しから
 * 分かるようにする。cross-package scenario はこれを複数組み合わせて [SnapshotScenario] に渡す。
 */
internal fun inputFileSpec(
    packageName: String,
    declarations: List<TypeSpec>,
): FileSpec {
    val fileName = declarations.firstOrNull()?.name?.let { "$packageName.$it" } ?: packageName
    return FileSpec
        .builder(packageName, fileName)
        .apply { declarations.forEach { addType(it) } }
        .build()
}

internal fun inputFileSpec(
    packageName: String,
    vararg declarations: TypeSpec,
): FileSpec = inputFileSpec(packageName, declarations.toList())

/** curated な (label -> scenario) 群を [Generator] に。arb は representative からの一様抽選。 */
internal fun Generator.Companion.snapshotScenarios(vararg cases: Pair<String, SnapshotScenario>): Generator<SnapshotScenario> =
    generator {
        cases.forEach { (label, scenario) -> label case scenario }
        Arb.of(cases.map { it.second })
    }
