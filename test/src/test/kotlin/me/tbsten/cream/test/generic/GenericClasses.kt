package me.tbsten.cream.test.generic

import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo

@CopyTo(GenericTargetWithThreeTypeArg::class)
@CopyFrom(GenericTargetWithThreeTypeArg::class)
class GenericSourceWithTwoTypeArg<
        @CopyFrom.Map("Aaa") @CopyTo.Map("Aaa") A,
        @CopyFrom.Map("Bbb") @CopyTo.Map("Bbb") B,
        >(
    val a: A?,
    val b: List<B>,
) where A : Comparable<String>, A : CharSequence

class GenericTargetWithThreeTypeArg<Aaa, Bbb, Ccc>(
    val a: Aaa?,
    val b: List<Bbb>,
    val c: Ccc,
) where Aaa : Comparable<String>, Aaa : CharSequence

@CopyTo(GenericTargetWithTwoTypeArg::class)
@CopyFrom(GenericTargetWithTwoTypeArg::class)
class GenericSourceWithThreeTypeArg<
        @CopyFrom.Map("Aaa")
        A,
        @CopyFrom.Map("Bbb")
        B,
        C>(
    val a: A?,
    val b: List<B>,
    val c: C
) where A : Comparable<String>, A : CharSequence

class GenericTargetWithTwoTypeArg<Aaa, Bbb>(
    val a: Aaa?,
    val b: List<Bbb>,
) where Aaa : Comparable<String>, Aaa : CharSequence
