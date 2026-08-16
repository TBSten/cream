package me.tbsten.cream.konsist

import io.kotest.core.spec.style.FreeSpec
import me.tbsten.cream.konsist.manifest.creamKspTestManifest

/**
 * [creamKspTestManifest]（cream-ksp/src/test の目録）の検証 spec。deny by default は配置と
 * 中身の両方に効く: manifest に列挙されていない .kt も、列挙されていないトップレベル宣言も
 * 違反（「manifest に書いてないことは書けない」）。検査本体は [manifestSpec]（共通部）。
 *
 * manifest driven development の第 2 弾（PR ②）。対象はテストコードそのもの
 * （feature 5 種 / scenario / testing 基盤 — 散文規約は .claude/rules/ksp-test.md）。
 * 規約と実態のズレ（5 種のはずが 3 種、など）は manifest 側に TODO 付きの明示的な例外
 * （baseline）として現れる。テストの依存方向は [CreamKspTestDependencyTest] が受け持つ。
 */
internal class CreamKspTestManifestTest :
    FreeSpec({
        manifestSpec(
            manifest = creamKspTestManifest(),
            manifestPath = "konsistTest/src/test/kotlin/me/tbsten/cream/konsist/manifest/CreamKspTestManifest.kt",
        )
    })
