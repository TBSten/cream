package me.tbsten.cream.ksp.testing.generator.util

import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.generator.GeneratorValue

/**
 * Combines [a] and [b] into a `Generator<Pair>` of the full cartesian product (every left × every
 * right). Customize how the two labels are merged via [label] (defaults to a ", " join).
 */
internal fun <A, B> cartesian(
    a: Generator<A>,
    b: Generator<B>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
): Generator<Pair<A, B>> =
    object : Generator<Pair<A, B>> {
        override fun representativeValues(): Sequence<GeneratorValue<Pair<A, B>>> =
            sequence {
                val rights = b.representativeValues().toList()
                a.representativeValues().forEach { leftValue ->
                    rights.forEach { rightValue -> yield(leftValue.pairedWith(rightValue, label)) }
                }
            }

        override fun arb(): Arb<Pair<A, B>> = arbitrary { rng -> a.arb().sample(rng).value to b.arb().sample(rng).value }
    }

private fun <A, B> GeneratorValue<A>.pairedWith(
    other: GeneratorValue<B>,
    label: (String?, String?) -> String?,
): GeneratorValue<Pair<A, B>> =
    GeneratorValue(
        label = label(this.label, other.label),
        value = value to other.value,
    )

/**
 * Combines [a], [b] and [c] into a `Generator<Triple>` of the full cartesian product, built on the
 * 2-arg [cartesian] (nested [Pair], then mapped to [Triple]). Labels are folded pairwise with [label]
 * — the same convention as the [combine] family. Kotlin has no built-in tuple beyond [Triple], so for
 * 4+ axes use the [combine] family (with a transform) or [combineToList].
 */
internal fun <A, B, C> cartesian(
    a: Generator<A>,
    b: Generator<B>,
    c: Generator<C>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
): Generator<Triple<A, B, C>> =
    cartesian(cartesian(a, b, label), c, label)
        .map { (ab, cValue) -> Triple(ab.first, ab.second, cValue) }

/** Combines two generators into one via [transform] (full cartesian product). Merge labels via [label]. */
internal fun <A, B, R> Generator.Companion.combine(
    a: Generator<A>,
    b: Generator<B>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
    transform: (A, B) -> R,
): Generator<R> = cartesian(a, b, label).map { (x, y) -> transform(x, y) }

/** Combines three generators into one via [transform] (full cartesian product). Merge labels via [label]. */
internal fun <A, B, C, R> Generator.Companion.combine(
    a: Generator<A>,
    b: Generator<B>,
    c: Generator<C>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
    transform: (A, B, C) -> R,
): Generator<R> = cartesian(cartesian(a, b, label), c, label).map { (ab, value) -> transform(ab.first, ab.second, value) }

/** Combines four generators into one via [transform] (full cartesian product). Merge labels via [label]. */
internal fun <A, B, C, D, R> Generator.Companion.combine(
    a: Generator<A>,
    b: Generator<B>,
    c: Generator<C>,
    d: Generator<D>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
    transform: (A, B, C, D) -> R,
): Generator<R> =
    cartesian(cartesian(cartesian(a, b, label), c, label), d, label)
        .map { (abc, value) -> transform(abc.first.first, abc.first.second, abc.second, value) }

/** Combines five generators into one via [transform] (full cartesian product). Merge labels via [label]. */
internal fun <A, B, C, D, E, R> Generator.Companion.combine(
    a: Generator<A>,
    b: Generator<B>,
    c: Generator<C>,
    d: Generator<D>,
    e: Generator<E>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
    transform: (A, B, C, D, E) -> R,
): Generator<R> =
    cartesian(cartesian(cartesian(cartesian(a, b, label), c, label), d, label), e, label)
        .map { (abcd, value) -> transform(abcd.first.first.first, abcd.first.first.second, abcd.first.second, abcd.second, value) }

/** Combines six generators into one via [transform] (full cartesian product). Merge labels via [label]. */
internal fun <A, B, C, D, E, F, R> Generator.Companion.combine(
    a: Generator<A>,
    b: Generator<B>,
    c: Generator<C>,
    d: Generator<D>,
    e: Generator<E>,
    f: Generator<F>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
    transform: (A, B, C, D, E, F) -> R,
): Generator<R> =
    cartesian(cartesian(cartesian(cartesian(cartesian(a, b, label), c, label), d, label), e, label), f, label)
        .map { (abcde, value) ->
            transform(
                abcde.first.first.first.first,
                abcde.first.first.first.second,
                abcde.first.first.second,
                abcde.first.second,
                abcde.second,
                value,
            )
        }

/**
 * Combines a list of generators into a `Generator<List>` of the full cartesian product of their reps.
 * Merge the per-element labels via [label] (defaults to a ", " join).
 */
internal fun <T> List<Generator<T>>.combineToList(label: (List<String?>) -> String? = { labels -> labels.filterNotNull().joinToString(", ") }): Generator<List<T>> {
    val generators = this
    return object : Generator<List<T>> {
        override fun representativeValues(): Sequence<GeneratorValue<List<T>>> =
            sequence {
                if (generators.isEmpty()) {
                    yield(GeneratorValue(label = null, value = emptyList()))
                    return@sequence
                }
                val perGenerator = generators.map { it.representativeValues().toList() }
                if (perGenerator.any { it.isEmpty() }) return@sequence
                cartesianProduct(perGenerator).forEach { combo -> yield(combo.toListValue(label)) }
            }

        override fun arb(): Arb<List<T>> = arbitrary { rng -> generators.map { it.arb().sample(rng).value } }
    }
}

/**
 * Vary-one-at-a-time pair: baseline (first rep of each) + each axis varied once — **not** the full
 * cartesian product (see [cartesian]). Opt-in for axes whose product would explode. Merge labels via [label].
 */
internal fun <A, B> varyingOnePair(
    a: Generator<A>,
    b: Generator<B>,
    label: (String?, String?) -> String? = { labelA, labelB -> listOfNotNull(labelA, labelB).joinToString(", ") },
): Generator<Pair<A, B>> =
    object : Generator<Pair<A, B>> {
        override fun representativeValues(): Sequence<GeneratorValue<Pair<A, B>>> =
            sequence {
                val lefts = a.representativeValues().toList()
                val rights = b.representativeValues().toList()
                if (lefts.isEmpty() || rights.isEmpty()) return@sequence
                val left0 = lefts.first()
                val right0 = rights.first()
                yield(left0.pairedWith(right0, label))
                lefts.drop(1).forEach { yield(it.pairedWith(right0, label)) }
                rights.drop(1).forEach { yield(left0.pairedWith(it, label)) }
            }

        override fun arb(): Arb<Pair<A, B>> = arbitrary { rng -> a.arb().sample(rng).value to b.arb().sample(rng).value }
    }

/** Vary-one-at-a-time list version of [combineToList] (opt-in). Merge labels via [label]. */
internal fun <T> List<Generator<T>>.combineToListVaryingOne(label: (List<String?>) -> String? = { labels -> labels.filterNotNull().joinToString(", ") }): Generator<List<T>> {
    val generators = this
    return object : Generator<List<T>> {
        override fun representativeValues(): Sequence<GeneratorValue<List<T>>> =
            sequence {
                if (generators.isEmpty()) {
                    yield(GeneratorValue(label = null, value = emptyList()))
                    return@sequence
                }
                val perGenerator = generators.map { it.representativeValues().toList() }
                if (perGenerator.any { it.isEmpty() }) return@sequence
                val baseline = perGenerator.map { it.first() }
                yield(baseline.toListValue(label))
                perGenerator.forEachIndexed { index, reps ->
                    reps.drop(1).forEach { rep ->
                        val variant = baseline.toMutableList().also { it[index] = rep }
                        yield(variant.toListValue(label))
                    }
                }
            }

        override fun arb(): Arb<List<T>> = arbitrary { rng -> generators.map { it.arb().sample(rng).value } }
    }
}

private fun <T> List<GeneratorValue<T>>.toListValue(label: (List<String?>) -> String?): GeneratorValue<List<T>> =
    GeneratorValue(
        label = label(map { it.label }),
        value = map { it.value },
    )

private fun <T> cartesianProduct(lists: List<List<T>>): Sequence<List<T>> =
    if (lists.isEmpty()) {
        sequenceOf(emptyList())
    } else {
        lists.first().asSequence().flatMap { head ->
            cartesianProduct(lists.drop(1)).map { tail -> listOf(head) + tail }
        }
    }
