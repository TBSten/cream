package me.tbsten.cream.test.generic

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo

@CopyTo(GenericTargetWithThreeTypeArg::class)
@CopyFrom(GenericTargetWithThreeTypeArg::class)
data class GenericSourceWithTwoTypeArg<
    @CopyFrom.Map("Aaa")
    @CopyTo.Map("Aaa")
    A,
    @CopyFrom.Map("Bbb")
    @CopyTo.Map("Bbb")
    B,
>(
    val a: A?,
    val b: List<B>,
    val d: Int,
) where A : Comparable<String>, A : CharSequence

data class GenericTargetWithThreeTypeArg<Aaa, Bbb, Ccc>(
    val a: Aaa?,
    val b: List<Bbb>,
    val c: Ccc,
    val d: Int,
) where Aaa : Comparable<String>, Aaa : CharSequence

@CopyTo(GenericTargetWithTwoTypeArg::class)
@CopyFrom(GenericTargetWithTwoTypeArg::class)
data class GenericSourceWithThreeTypeArg<
    @CopyFrom.Map("Aaa")
    A,
    @CopyFrom.Map("Bbb")
    B,
    C,
>(
    val a: A?,
    val b: List<B>,
    val c: C,
) where A : Comparable<String>, A : CharSequence

data class GenericTargetWithTwoTypeArg<Aaa, Bbb>(
    val a: Aaa?,
    val b: List<Bbb>,
) where Aaa : Comparable<String>, Aaa : CharSequence
