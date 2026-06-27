package me.tbsten.cream.test.exclude

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class ExcludeTest :
    FreeSpec({

        "CopyFrom.Exclude - caller-supplied value is reflected" {
            val source: CopyFromState = CopyFromState.Loading(name = "alice", count = 5)
            val result = source.copyToCopyFromStateSuccess(count = 99)
            result.name shouldBe "alice"
            result.count shouldBe 99
        }

        "CopyFrom.Exclude - source value is not auto-copied" {
            val source: CopyFromState = CopyFromState.Loading(name = "bob", count = 5)
            val result = source.copyToCopyFromStateSuccess(count = 0)
            result.count shouldBe 0
        }

        "CopyTo.Exclude - caller-supplied value is reflected" {
            val source = CopyToSource(name = "bob", count = 3)
            val result = source.copyToCopyToTarget(count = 42)
            result.name shouldBe "bob"
            result.count shouldBe 42
        }

        "CombineFrom.Exclude - explicit value is used instead of auto-copied source value" {
            val loading = CombineFromLoading(itemId = "item-1")
            val action = CombineFromAction(data = "payload")
            val result =
                loading.copyToCombineFromSuccess(
                    combineFromAction = action,
                    data = "explicit-data",
                    extra = 7,
                )
            result.itemId shouldBe "item-1"
            result.data shouldBe "explicit-data"
            result.extra shouldBe 7
        }

        "CombineTo.Exclude - explicit value is used instead of auto-copied source value" {
            val source = CombineToSource(itemId = "item-2", sessionId = "session-original")
            val result = source.copyToCombineToTarget(sessionId = "session-new", extra = 0)
            result.itemId shouldBe "item-2"
            result.sessionId shouldBe "session-new"
        }

        "SealedCopy.Exclude - caller-supplied count is reflected" {
            val state: SealedCopyExcludeState =
                SealedCopyExcludeState.Loading(name = "init", count = 1)
            val result = state.copy(count = 99)
            result shouldBe SealedCopyExcludeState.Loading(name = "init", count = 99)
        }

        "SealedCopy.Exclude - non-excluded property is still auto-copied" {
            val state: SealedCopyExcludeState =
                SealedCopyExcludeState.Success(name = "keep", count = 0, data = "d")
            val result = state.copy(count = 5)
            (result as SealedCopyExcludeState.Success).name shouldBe "keep"
            result.count shouldBe 5
            result.data shouldBe "d"
        }

        "CopyToChildren.Exclude - copyToLoading uses caller-supplied count" {
            val state: CopyToChildrenExcludeState =
                CopyToChildrenExcludeState.Loading(sessionId = "s1", count = 0)
            val loading = state.copyToCopyToChildrenExcludeStateLoading(count = 10)
            loading.sessionId shouldBe "s1"
            loading.count shouldBe 10
        }

        "CopyToChildren.Exclude - copyToSuccess also uses caller-supplied count" {
            val state: CopyToChildrenExcludeState =
                CopyToChildrenExcludeState.Loading(sessionId = "s2", count = 0)
            val success =
                state.copyToCopyToChildrenExcludeStateSuccess(
                    count = 20,
                    data = "payload",
                )
            success.sessionId shouldBe "s2"
            success.count shouldBe 20
        }
    })
