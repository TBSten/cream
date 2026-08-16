package me.tbsten.cream.konsist

import com.lemonappdev.konsist.api.provider.KoNameProvider
import com.lemonappdev.konsist.api.provider.modifier.KoVisibilityModifierProvider
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.konsist.manifest.creamKspManifest
import me.tbsten.cream.konsist.support.ProjectScope

/**
 * [creamKspManifest]（cream-ksp/src/main の目録）の検証 spec。deny by default は配置と中身の
 * 両方に効く: manifest に列挙されていない .kt も、列挙されていないトップレベル宣言も違反
 * （「manifest に書いてないことは書けない」）。検査本体は [manifestSpec]（共通部）。
 *
 * manifest driven development の第 1 弾（PR ①）。cream-ksp 側の既存 3 spec
 * （AllKotlinFilesTest / feature.ArchTest / core.ArchTest）はまだ廃止せず併存させる —
 * 両方 green であることが「manifest が既存規約を再現できている」ことの等価性検証になる。
 */
internal class CreamKspManifestTest :
    FreeSpec({
        manifestSpec(
            manifest = creamKspManifest,
            manifestPath = "konsistTest/src/test/kotlin/me/tbsten/cream/konsist/manifest/CreamKspManifest.kt",
        )

        "cream-ksp/main の public なトップレベル宣言は CreamSymbolProcessorProvider だけ" {
            // explicitApi() 下の実測。エントリポイント以外の公開 API を生やさない。
            val mainFiles =
                ProjectScope.projectFiles.filter { it.moduleName == "cream-ksp" && it.sourceSetName == "main" }
            val publicDeclarationNames =
                mainFiles
                    .flatMap { file -> file.declarations(includeNested = false, includeLocal = false) }
                    .filter { it is KoVisibilityModifierProvider && it.hasPublicOrDefaultModifier }
                    .map { if (it is KoNameProvider) it.name else it.toString() }
            withClue("public / 修飾なしのトップレベル宣言: $publicDeclarationNames") {
                publicDeclarationNames shouldBe listOf("CreamSymbolProcessorProvider")
            }
        }
    })
