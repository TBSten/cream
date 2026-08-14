package me.tbsten.cream.test.childOptionals

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FreeSpec({
        "leaf 固有のプロパティごとにアクセサが生成され、該当する子から値が取れる" {
            val downloading: DownloadState = DownloadState.Downloading(id = "a", progress = 42)
            downloading.progress shouldBe 42
            downloading.resultPath shouldBe null
        }

        "該当しない子ではアクセサは null を返す" {
            val idle: DownloadState = DownloadState.Idle
            idle.progress shouldBe null
            idle.resultPath shouldBe null
        }

        "body 宣言プロパティもアクセサ生成の対象になる" {
            val done: DownloadState = DownloadState.Done(id = "a", resultPath = "dir/file.txt")
            val idle: DownloadState = DownloadState.Idle
            done.fileName shouldBe "file.txt"
            idle.fileName shouldBe null
        }

        "親で見えるプロパティはアクセサ化されず member がそのまま使える" {
            val downloading: DownloadState = DownloadState.Downloading(id = "download-1", progress = 0)
            downloading.id shouldBe "download-1"
        }

        "sweep 対象のプロパティでも @ParentOptional の propertyName 指定が尊重される" {
            val loggedIn: AuthState = AuthState.LoggedIn(userName = "tbs")
            val loggedOut: AuthState = AuthState.LoggedOut
            loggedIn.userNameOrNull shouldBe "tbs"
            loggedOut.userNameOrNull shouldBe null
        }

        "親で見える override プロパティでも @ParentOptional の rename があればアクセサが生成される" {
            val editing: FormState = FormState.Editing(input = "hello")
            val empty: FormState = FormState.Empty
            editing.inputOrNull shouldBe "hello"
            empty.inputOrNull shouldBe null
            // member はそのまま使える
            editing.input shouldBe "hello"
        }

        "@ChildOptionals の propertyName テンプレートが sweep 全体に効き、@ParentOptional は明示した引数だけ上書きする" {
            val sending: TransferState = TransferState.Sending(bytes = 42, target = "s3", path = "/tmp/a")
            val idleTransfer: TransferState = TransferState.Waiting
            sending.bytesOrNull shouldBe 42
            sending.targetOrNull shouldBe "s3"
            sending.destination shouldBe "/tmp/a"
            idleTransfer.bytesOrNull shouldBe null
            idleTransfer.targetOrNull shouldBe null
            idleTransfer.destination shouldBe null
        }

        "中間 sealed 型自身の @ParentOptional プロパティも sweep され、配下の全 leaf で値が取れる" {
            val streaming: ConnectionState = ConnectionState.Streaming(sessionId = "s-1", bitrate = 128)
            val waiting: ConnectionState = ConnectionState.Waiting(sessionId = "s-2")
            val disconnected: ConnectionState = ConnectionState.Disconnected
            streaming.sessionId shouldBe "s-1"
            waiting.sessionId shouldBe "s-2"
            disconnected.sessionId shouldBe null
            // leaf 固有プロパティも従来どおり sweep される
            streaming.bitrate shouldBe 128
            waiting.bitrate shouldBe null
        }
    })
