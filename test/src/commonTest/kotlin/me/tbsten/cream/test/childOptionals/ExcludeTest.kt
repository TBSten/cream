package me.tbsten.cream.test.childOptionals

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludeTest :
    FreeSpec({
        "@ChildOptionals.Exclude を付けたプロパティにはアクセサが生成されず、兄弟プロパティは生成される" {
            val uploading: UploadState = UploadState.Uploading(progress = 42, tempToken = "secret")
            val idle: UploadState = UploadState.Idle
            // 除外していない兄弟 progress はアクセサが生成される
            uploading.progress shouldBe 42
            idle.progress shouldBe null
            // tempToken は手書き拡張に解決される (= 生成アクセサが存在しない証明: 生成されていれば
            // conflicting overloads でコンパイルできない)
            uploading.tempToken shouldBe "manual"
        }

        "同名にマージされる子のうち除外された子は when 分岐から外れる" {
            val email: NotifyState = NotifyState.Email(address = "a@example.com", subject = "hi")
            val sms: NotifyState = NotifyState.Sms(address = "090-0000-0000")
            val silent: NotifyState = NotifyState.Silent
            // address は Sms の分岐だけが残る (Email は除外) -> Email インスタンスでは null
            sms.address shouldBe "090-0000-0000"
            email.address shouldBe null
            silent.address shouldBe null
            // Email の非除外プロパティ subject は従来どおり生成される
            email.subject shouldBe "hi"
        }

        "@ParentOptional で明示的に opt-in したプロパティは @ChildOptionals.Exclude より優先され生成される" {
            val paid: PaymentState = PaymentState.Paid(amount = 100)
            val unpaid: PaymentState = PaymentState.Unpaid
            paid.amount shouldBe 100
            unpaid.amount shouldBe null
        }
    })
