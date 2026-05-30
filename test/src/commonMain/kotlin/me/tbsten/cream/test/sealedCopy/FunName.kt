package me.tbsten.cream.test.sealedCopy

import me.tbsten.cream.CopyTargetSimpleName
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.SealedCopy

// DefaultCopyFunctionName resolves to "copy" for @SealedCopy; the second annotation uses a
// token that renders the sealed type's own name.
@SealedCopy(funName = "my" + DefaultCopyFunctionName)
@SealedCopy(funName = "to" + CopyTargetSimpleName)
sealed interface FunNameSealed {
    val id: String

    data class A(
        override val id: String,
    ) : FunNameSealed

    data class B(
        override val id: String,
        val x: Int,
    ) : FunNameSealed
}
