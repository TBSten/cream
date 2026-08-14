package me.tbsten.cream.test.childOptionals

import me.tbsten.cream.ChildOptionals
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultAccessorPropertyName
import me.tbsten.cream.ParentOptional

@ChildOptionals
sealed interface DownloadState {
    // 親で見えるプロパティ (override 含む) はアクセサ生成の対象外
    val id: String

    data class Downloading(
        override val id: String,
        val progress: Int,
    ) : DownloadState

    data class Done(
        override val id: String,
        val resultPath: String,
    ) : DownloadState {
        // body 宣言プロパティも対象になる
        val fileName: String get() = resultPath.substringAfterLast('/')
    }

    data object Idle : DownloadState {
        override val id: String = ""
    }
}

// @ChildOptionals の sweep 内でも @ParentOptional の propertyName 指定が尊重される
@ChildOptionals
sealed interface AuthState {
    data class LoggedIn(
        @ParentOptional(propertyName = "userNameOrNull") val userName: String,
    ) : AuthState

    data object LoggedOut : AuthState
}

// 親で見えるプロパティ (override) でも、明示的な @ParentOptional の rename があれば
// その名前でアクセサが生成される (rename なし override は従来どおり対象外)
@ChildOptionals
sealed interface FormState {
    val input: String

    data class Editing(
        @ParentOptional(propertyName = "inputOrNull") override val input: String,
    ) : FormState

    data object Empty : FormState {
        override val input: String = ""
    }
}

// sweep の引数はプロパティ側の @ParentOptional が「明示した分だけ」上書きされる
@ChildOptionals(
    propertyName = "${DefaultAccessorPropertyName}OrNull",
    visibility = CopyVisibility.INTERNAL,
)
sealed interface TransferState {
    data class Sending(
        val bytes: Int, // bytesOrNull
        @ParentOptional val target: String, // targetOrNull — 未指定なのでテンプレートが効く
        @ParentOptional(propertyName = "destination") val path: String, // destination、visibility は INTERNAL のまま
    ) : TransferState

    data object Waiting : TransferState
}

// 中間 sealed 型 (Active) 自身が宣言する @ParentOptional プロパティも sweep の対象になる
// (is Active の 1 分岐で配下の全 leaf をカバー)
@ChildOptionals
sealed interface ConnectionState {
    sealed class Active(
        @ParentOptional val sessionId: String,
    ) : ConnectionState

    class Streaming(
        sessionId: String,
        val bitrate: Int,
    ) : Active(sessionId)

    class Waiting(
        sessionId: String,
    ) : Active(sessionId)

    data object Disconnected : ConnectionState
}
