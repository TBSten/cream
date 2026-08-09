package me.tbsten.cream.test.childOptionals

import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.ParentOptional

// @ChildOptionals.Exclude で sweep から外したプロパティはアクセサが生成されない。
// 除外していない兄弟プロパティ (progress) は従来どおりアクセサが生成される。
@ChildOptionals
sealed interface UploadState {
    data class Uploading(
        val progress: Int,
        @ChildOptionals.Exclude val tempToken: String,
    ) : UploadState

    data object Idle : UploadState
}

// もし cream が UploadState.tempToken アクセサを生成していたら、同じレシーバ + 名前のこの手書き拡張
// プロパティは "conflicting overloads" のコンパイルエラーになる。このモジュールがコンパイルできること
// 自体が「除外プロパティにはアクセサが生成されていない」ことの証明。sentinel で読み取り経路も観測する。
val UploadState.tempToken: String
    get() = "manual"

// 同名にマージされる複数の子のうち、除外された子はマージ (when 分岐) から外れる。
// address: Sms の分岐だけが残り、Email 側は除外されるため Email インスタンスでは null を返す。
@ChildOptionals
sealed interface NotifyState {
    data class Email(
        @ChildOptionals.Exclude val address: String,
        val subject: String,
    ) : NotifyState

    data class Sms(
        val address: String,
    ) : NotifyState

    data object Silent : NotifyState
}

// @ParentOptional で明示的に opt-in したプロパティは、@ChildOptionals.Exclude が同時に付いていても
// 生成される (明示 opt-in が sweep opt-out に勝つ)。
@ChildOptionals
sealed interface PaymentState {
    data class Paid(
        @ParentOptional @ChildOptionals.Exclude val amount: Int,
    ) : PaymentState

    data object Unpaid : PaymentState
}
