package me.tbsten.cream.ksp.snapshot

import com.tschuchort.compiletesting.KotlinCompilation
import me.tbsten.cream.ksp.testing.assertMatchesSnapshot
import me.tbsten.cream.ksp.testing.compileWithCream
import me.tbsten.cream.ksp.testing.generatedSourceText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Pin every realistic shape of user-supplied KDoc content that cream is expected to
 * embed verbatim into the generated function's KDoc.
 *
 * Each test pins one realistic content shape from the issue discussion: one-line
 * summaries, multi-paragraph prose, embedded `@param` / `@return` / `@throws`
 * tags, inline links, Markdown lists, KDoc-inside-KDoc code fences, etc. We don't
 * exercise different code-generation paths here — the goal is to make any future
 * change that touches the description/examples render pipeline visible in review.
 *
 * Patterns also span every source annotation (`@CopyTo`, `@CopyFrom`,
 * `@CopyToChildren`, `@CombineTo`, `@CombineFrom`, `@CopyMapping`) so each one's
 * `kdoc` plumbing has at least one golden file pinned.
 *
 * Per-`Map` KDoc (`@CopyTo.Map(kdoc = ...)` on a property mapping) is
 * intentionally skipped: it would require extending the nested annotation and the
 * rendering pipeline, which is outside the scope of #64.
 */
internal class KDocPatternsSnapshotTest {
    private fun runSnapshot(
        snapshotName: String,
        sourceCode: String,
    ) {
        val result = compileWithCream(sourceCode)
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode, result.messages)
        assertMatchesSnapshot(snapshotName) {
            "Generated" facetOf result.generatedSourceText()
            "Input" facetOf sourceCode
        }
    }

    @Test
    fun `one-liner summary`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.OneLiner",
            """
            package snap.kdoc.oneliner

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "Loading から Success への状態遷移。"),
            )
            data class Source(val shared: String)

            data class Target(val shared: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `multi-paragraph description`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.MultiParagraph",
            """
            package snap.kdoc.multiparagraph

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        フェッチ完了時に Loading から Success へ遷移させる。

                        userName / password は前状態から引き継がれるため、
                        呼び出し側では新しく取得した data のみ渡せばよい。
                    $q,
                ),
            )
            data class Source(val userName: String, val password: String)

            data class Target(val userName: String, val password: String, val data: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `do-not-use warning`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.Warning",
            """
            package snap.kdoc.warning

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        エラー状態からの復帰には使用しないこと。
                        その場合は retry() 経由で再フェッチすること。
                    $q,
                ),
            )
            data class Source(val shared: String)

            data class Target(val shared: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-param tags`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.AtParam",
            """
            package snap.kdoc.atparam

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        @param data API から取得したアイテム詳細。null 不可。
                        @param itemId 省略時は遷移元の itemId を引き継ぐ。
                    $q,
                ),
            )
            data class Source(val itemId: String)

            data class Target(val itemId: String, val data: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-return tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtReturn",
            """
            package snap.kdoc.atreturn

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@return 入力が確定済みになった注文状態。以降は編集不可。"),
            )
            data class Source(val shared: String)

            data class Target(val shared: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-throws tag on CopyFrom`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtThrowsCopyFrom",
            """
            package snap.kdoc.atthrowscopyfrom

            import me.tbsten.cream.CopyFrom
            import me.tbsten.cream.KDoc

            data class RemoteUser(val email: String)

            @CopyFrom(
                RemoteUser::class,
                kdoc = KDoc(description = "@throws IllegalArgumentException email が空文字の場合。"),
            )
            data class User(val email: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-see tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtSee",
            """
            package snap.kdoc.atsee

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@see copyToLoading 逆方向の遷移にはこちらを使う。"),
            )
            data class Source(val shared: String)

            data class Target(val shared: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-sample tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtSample",
            """
            package snap.kdoc.atsample

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@sample com.example.samples.cartSummarySample"),
            )
            data class Source(val items: List<String>)

            data class Target(val items: List<String>)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-since tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtSince",
            """
            package snap.kdoc.atsince

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@since 2.3.0 API v2 移行に伴い追加。"),
            )
            data class Source(val body: String)

            data class Target(val body: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `inline symbol link`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.InlineSymbolLink",
            """
            package snap.kdoc.inlinesymbollink

            import me.tbsten.cream.CopyFrom
            import me.tbsten.cream.KDoc

            data class Entity(val id: String)

            @CopyFrom(
                Entity::class,
                kdoc = KDoc(description = "永続化層の [Entity] からドメインモデルへ変換する。"),
            )
            data class DomainModel(val id: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `inline code span`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.InlineCode",
            """
            package snap.kdoc.inlinecode

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "`isLoading` は常に `false` に確定する点に注意。"),
            )
            data class Source(val isLoading: Boolean)

            data class Target(val isLoading: Boolean)
            """.trimIndent(),
        )
    }

    @Test
    fun `description with embedded code fence`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.CodeFenceInDescription",
            """
            package snap.kdoc.codefenceindescription

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        使用例:
                        ${'`'}${'`'}${'`'}kotlin
                        val next = prev.copyToTarget(answer = "yes")
                        ${'`'}${'`'}${'`'}
                    $q,
                ),
            )
            data class Source(val shared: String)

            data class Target(val shared: String, val answer: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `markdown bullet list`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.BulletList",
            """
            package snap.kdoc.bulletlist

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        下書きから公開状態へ遷移する。

                        - publishedAt は呼び出し時に必須
                        - tags は引き継がれる
                        - reviewerComment は破棄される
                    $q,
                ),
            )
            data class Source(val tags: List<String>, val reviewerComment: String?)

            data class Target(val tags: List<String>, val publishedAt: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-suppress tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtSuppress",
            """
            package snap.kdoc.atsuppress

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@suppress 内部遷移用。公開ドキュメントには出さない。"),
            )
            data class Source(val shared: String)

            data class Target(val shared: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `migration guidance`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.MigrationGuidance",
            """
            package snap.kdoc.migrationguidance

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        旧形式への変換。新規コードでは copyToResultV2 を使うこと。
                        本関数は v3.0 で削除予定。
                    $q,
                ),
            )
            data class Source(val payload: String)

            data class Target(val payload: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `domain-language identifiers`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.DomainLanguage",
            """
            package snap.kdoc.domainlanguage

            import me.tbsten.cream.CopyFrom
            import me.tbsten.cream.KDoc

            data class 注文Entity(val 税込金額: Int)

            @CopyFrom(
                注文Entity::class,
                kdoc = KDoc(description = "DB の注文レコードを業務ドメインの注文集約へ変換する。金額は税込で保持する。"),
            )
            data class 注文(val 税込金額: Int)
            """.trimIndent(),
        )
    }

    @Test
    fun `external URL link`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.ExternalUrl",
            """
            package snap.kdoc.externalurl

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "リクエスト仕様は [API ドキュメント](https://example.com/docs/orders) を参照。"),
            )
            data class Source(val amount: Int)

            data class Target(val amount: Int)
            """.trimIndent(),
        )
    }

    @Test
    fun `numbered list for CombineTo`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.NumberedList",
            """
            package snap.kdoc.numberedlist

            import me.tbsten.cream.CombineTo
            import me.tbsten.cream.KDoc

            @CombineTo(
                Checkout::class,
                kdoc = KDoc(
                    description = $q
                        カート確定の手順:
                        1. CartState と PaymentInfo を用意する
                        2. cart.copyToCheckout(paymentInfo = ...) を呼ぶ
                        3. 戻り値を repository.submit() に渡す
                    $q,
                ),
            )
            data class CartState(val total: Int)

            data class PaymentInfo(val method: String)

            data class Checkout(val total: Int, val method: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `thread or coroutine safety note`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.ThreadSafety",
            """
            package snap.kdoc.threadsafety

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "純粋な変換のみで副作用なし。任意のディスパッチャから安全に呼べる。"),
            )
            data class Source(val payload: String)

            data class Target(val payload: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `lossy conversion warning`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.LossyConversion",
            """
            package snap.kdoc.lossyconversion

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(
                    description = $q
                        詳細から要約への変換。
                        本文 body と添付 attachments は要約側に存在しないため破棄される。
                    $q,
                ),
            )
            data class Source(val body: String, val attachments: List<String>)

            data class Target(val title: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `performance note`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.PerformanceNote",
            """
            package snap.kdoc.performancenote

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "毎回新インスタンスを生成する。高頻度ループ内での呼び出しは避ける。"),
            )
            data class Source(val frame: Long)

            data class Target(val frame: Long)
            """.trimIndent(),
        )
    }

    @Test
    fun `at-receiver tag`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.AtReceiver",
            """
            package snap.kdoc.atreceiver

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "@receiver 承認前の申請。status は Pending である前提。"),
            )
            data class Source(val status: String)

            data class Target(val status: String, val approvedBy: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `domain invariant on CopyFrom`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.DomainInvariant",
            """
            package snap.kdoc.domaininvariant

            import me.tbsten.cream.CopyFrom
            import me.tbsten.cream.KDoc

            data class Quote(val amount: Int)

            @CopyFrom(
                Quote::class,
                kdoc = KDoc(description = "確定見積から契約を生成する。契約金額は見積金額と必ず一致する（値引きは別途）。"),
            )
            data class Contract(val amount: Int)
            """.trimIndent(),
        )
    }

    @Test
    fun `CombineTo last-wins ordering`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.CombineToLastWins",
            """
            package snap.kdoc.combinetolastwins

            import me.tbsten.cream.CombineTo
            import me.tbsten.cream.KDoc

            @CombineTo(
                MergedState::class,
                kdoc = KDoc(
                    description = $q
                        ServerState と LocalEdit を統合する。
                        同名プロパティは後勝ち（LocalEdit 側）が優先される点に注意。
                    $q,
                ),
            )
            data class ServerState(val title: String)

            data class LocalEdit(val title: String)

            data class MergedState(val title: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `CopyToChildren shared description`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.CopyToChildrenShared",
            """
            package snap.kdoc.copytochildrenshared

            import me.tbsten.cream.CopyToChildren
            import me.tbsten.cream.KDoc

            @CopyToChildren(
                kdoc = KDoc(description = "UiState 内の任意の状態へ遷移するための生成関数。遷移元の共通プロパティは引き継がれる。"),
            )
            sealed interface UiState {
                val sessionId: String

                data class Loading(override val sessionId: String) : UiState
                data class Success(override val sessionId: String, val data: String) : UiState
            }
            """.trimIndent(),
        )
    }

    @Test
    fun `CopyMapping for library boundary`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.CopyMappingLibraryBoundary",
            """
            package snap.kdoc.copymappinglibraryboundary

            import me.tbsten.cream.CopyMapping
            import me.tbsten.cream.KDoc

            data class StripeCustomer(val id: String, val email: String)

            data class AppUser(val id: String, val email: String)

            @CopyMapping(
                StripeCustomer::class,
                AppUser::class,
                kdoc = KDoc(description = "外部 SDK の StripeCustomer を自前ドメインへ隔離変換する腐敗防止層。"),
            )
            private object StripeUserMapping
            """.trimIndent(),
        )
    }

    // Pattern 27 (`@CopyTo.Map(kdoc = ...)`) would need a separate `kdoc` field on
    // the nested `Map` annotation and a new render path that surfaces the per-property
    // note in the parent function's KDoc (most naturally as a `@param` line). That is
    // outside the scope of #64 — left intentionally unimplemented here.

    @Test
    fun `TODO note for known limitation`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.TodoNote",
            """
            package snap.kdoc.todonote

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                Target::class,
                kdoc = KDoc(description = "TODO: ネストした metadata の深いコピーは未対応。浅いコピーになる。"),
            )
            data class Source(val metadata: Map<String, Any>)

            data class Target(val metadata: Map<String, Any>)
            """.trimIndent(),
        )
    }

    @Test
    fun `CombineFrom multi-source intent on target`() {
        runSnapshot(
            "KDocPatternsSnapshotTest.CombineFromIntent",
            """
            package snap.kdoc.combinefromintent

            import me.tbsten.cream.CombineFrom
            import me.tbsten.cream.KDoc

            data class LoadingState(val itemId: String)

            data class SuccessAction(val data: String)

            @CombineFrom(
                LoadingState::class,
                SuccessAction::class,
                kdoc = KDoc(description = "進行中の状態とアクション結果を合成して成功状態を組み立てる。lastUpdateAt は呼び出し側で指定する。"),
            )
            data class SuccessState(val itemId: String, val data: String, val lastUpdateAt: String)
            """.trimIndent(),
        )
    }

    @Test
    fun `rich multi-tag documentation`() {
        val q = "\"\"\""
        runSnapshot(
            "KDocPatternsSnapshotTest.RichMultiTag",
            """
            package snap.kdoc.richmultitag

            import me.tbsten.cream.CopyTo
            import me.tbsten.cream.KDoc

            @CopyTo(
                OrderConfirmed::class,
                kdoc = KDoc(
                    description = $q
                        カート確定処理。在庫確保後にこの遷移を行うこと。

                        @param paidAt 決済完了時刻。
                        @return 確定済みの注文。以降キャンセルは別フローになる。
                        @throws IllegalStateException カートが空の場合。
                        @see copyToOrderDraft
                        @sample com.example.samples.confirmOrderSample
                        @since 1.4.0
                    $q,
                ),
            )
            data class OrderCart(val items: List<String>)

            data class OrderConfirmed(val items: List<String>, val paidAt: String)
            """.trimIndent(),
        )
    }
}
