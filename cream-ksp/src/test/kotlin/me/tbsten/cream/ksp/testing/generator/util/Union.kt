package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/**
 * Unions several same-typed generators into one: representative values are every generator's
 * representatives concatenated in order; the [Arb] picks one generator uniformly at random and
 * samples it. The dual of [cartesian] — a union of value spaces rather than a product.
 */
internal fun <T> List<Generator<T>>.union(): Generator<T> {
    require(isNotEmpty()) { "union requires at least one generator" }
    val generators = this

    return object : Generator<T> {
        override fun representativeValues(): Sequence<GeneratorValue<T>> = generators.asSequence().flatMap { it.representativeValues() }

        override fun arb(): Arb<T> = arbitrary { rng -> generators[rng.random.nextInt(generators.size)].arb().sample(rng).value }
    }
}

internal fun <T> union(vararg generators: Generator<T>): Generator<T> = generators.toList().union()

/**
 * Builder form of [union] that (re)labels each member as it is added, so a union of sub-generators
 * can namespace its cases instead of every member sharing one flat label space:
 * ```
 * union {
 *     "body"       case bodySweep          // -> "body/<member label>" (or "body[i]" if unlabelled)
 *     "targetKind" case targetKindCases
 *     case(siblings)                       // member labels kept as-is
 *     case(matching) { "matching/$it" }    // full label transform — same shape as mapLabel
 * }
 * ```
 * Members are unioned in registration order, identical to the [union] overloads above.
 */
internal fun <T> union(build: UnionBuilder<T>.() -> Unit): Generator<T> = UnionBuilderImpl<T>().apply(build).members.union()

/** Receiver of the [union] builder lambda; registers member generators, optionally (re)labelling them. */
internal interface UnionBuilder<T> {
    /** Add a member generator unchanged — its representatives keep their own labels. */
    fun case(generator: Generator<T>)

    /** Add a member, transforming each representative's label. Same shape as [mapLabel]. */
    fun case(
        generator: Generator<T>,
        label: (String?) -> String?,
    )

    /**
     * `"prefix" case generator` — prefix every representative label with `"prefix/"`, falling back
     * to `"prefix[i]"` for an unlabelled representative so members never collapse onto one label.
     */
    infix fun String.case(generator: Generator<T>)

    /**
     * 登録順に 0 始まりの連番プレフィックスを自動付与するサブスコープを開く。ブロック内の各
     * `"label" case generator` は `"<連番>[separator]label"` を prefix として親 union に追加される。
     * 連番は [length] 桁を [padChar] で詰め、`numberPrefix` と case 名の区切りは [separator]。
     *
     * ```
     * union {
     *     withNumberPrefix(length = 2) {
     *         "hoge" case g1   // = "00:hoge" case g1
     *         "fuga" case g2   // = "01:fuga" case g2
     *     }
     * }
     * ```
     */
    fun withNumberPrefix(
        length: Int = 2,
        padChar: Char = '0',
        separator: String = "--",
        block: NumberPrefixScope<T>.() -> Unit,
    )
}

/** [UnionBuilder.withNumberPrefix] のブロック receiver。`"label" case generator` を登録順に連番付きで親 union へ追加する。 */
internal interface NumberPrefixScope<T> {
    infix fun String.case(generator: Generator<T>)
}

private class UnionBuilderImpl<T> : UnionBuilder<T> {
    val members = mutableListOf<Generator<T>>()

    override fun case(generator: Generator<T>) {
        members += generator
    }

    override fun case(
        generator: Generator<T>,
        label: (String?) -> String?,
    ) {
        members += generator.mapLabel(label)
    }

    override infix fun String.case(generator: Generator<T>) {
        addPrefixed(this, generator)
    }

    override fun withNumberPrefix(
        length: Int,
        padChar: Char,
        separator: String,
        block: NumberPrefixScope<T>.() -> Unit,
    ) {
        var index = 0
        val scope =
            object : NumberPrefixScope<T> {
                override infix fun String.case(generator: Generator<T>) {
                    val number = index.toString().padStart(length, padChar)
                    index++
                    addPrefixed("$number$separator$this", generator)
                }
            }
        scope.block()
    }

    private fun addPrefixed(
        prefix: String,
        generator: Generator<T>,
    ) {
        members +=
            object : Generator<T> {
                override fun representativeValues(): Sequence<GeneratorValue<T>> =
                    generator.representativeValues().mapIndexed { index, value ->
                        GeneratorValue(
                            label = value.label?.let { "$prefix/$it" } ?: "$prefix[$index]",
                            value = value.value,
                        )
                    }

                override fun arb(): Arb<T> = generator.arb()
            }
    }
}
