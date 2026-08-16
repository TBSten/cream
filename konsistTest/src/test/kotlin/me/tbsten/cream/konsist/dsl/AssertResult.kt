package me.tbsten.cream.konsist.dsl

/**
 * [ManifestAssertionRule.assert] の結果。意味論・規律は [ManifestAssertionRule] を参照。
 */
internal sealed interface AssertResult {
    /** [finalize] 後の型。NoEffect が型レベルで消えるので、spec は Ok / Failure だけを扱えばよい。 */
    sealed interface Final : AssertResult

    /** 対象外（何も言うことがない）。[plus] の単位元。 */
    data object NoEffect : AssertResult

    /** このファイルの存在を manifest として承認し、検査して問題なし。 */
    data object Ok : AssertResult, Final

    /** 違反あり。[because] は 1 ファイル分の全違反（fail-fast にしない）。 */
    data class Failure(
        val because: List<String>,
    ) : AssertResult,
        Final

    /**
     * 結果の合成。可換・結合的で [NoEffect] が単位元。
     * lazy にしないのは、どの分岐でも [other] の評価が必要なため（全違反を一度に見せる）。
     */
    operator fun plus(other: AssertResult): AssertResult =
        when (this) {
            is Failure ->
                when (other) {
                    is Failure -> Failure(because + other.because)
                    Ok,
                    NoEffect,
                    -> this
                }

            Ok ->
                when (other) {
                    is Failure -> other
                    Ok,
                    NoEffect,
                    -> Ok
                }

            NoEffect -> other
        }
}

/**
 * 未確定（NoEffect = 誰も承認していない）を [onNoEffect] で確定させる終端。
 * 変換が必ず呼ぶ（deny by default の確定はここ）。
 *
 * @see ManifestAssertionRule
 */
internal fun AssertResult.finalize(onNoEffect: () -> AssertResult.Failure): AssertResult.Final =
    when (this) {
        AssertResult.NoEffect -> onNoEffect()
        AssertResult.Ok -> AssertResult.Ok
        is AssertResult.Failure -> this
    }
