package me.tbsten.cream.konsist.dsl

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration

/**
 * manifest の検査ルール。**rule の代数の大筋はここに書く**（DSL 意味論の大筋は [manifest]）。
 *
 * - 対象判定も検査も [assert] に畳む。対象外は [AssertResult.NoEffect]
 * - 合成は [plus]（両方を呼び [AssertResult.plus] で結合）。合成は**厳しくなる一方向**で、
 *   後から足した rule が先の Failure を取り消す手段はない。緩和の唯一の経路は manifest データの編集
 * - [AssertResult.Ok]（存在の承認）を返してよいのは entry から生成される標準 rule だけ。
 *   付随的な制約 rule は満足時も [AssertResult.NoEffect] を返す — でないと配置の deny by default が破れる
 * - 「誰も承認しなければ配置違反」は fold の要素では書けない（rule は他の rule の応答を観測できない）
 *   ため、変換が fold 結果に必ず [finalize] を適用して表現する
 */
internal fun interface ManifestAssertionRule {
    fun assert(file: KoFileDeclaration): AssertResult
}

/** @see ManifestAssertionRule */
internal operator fun ManifestAssertionRule.plus(other: ManifestAssertionRule): ManifestAssertionRule = ManifestAssertionRule { file -> this.assert(file) + other.assert(file) }

/** fold の初期値（単位元）。 */
internal val nothingRule: ManifestAssertionRule = ManifestAssertionRule { AssertResult.NoEffect }
