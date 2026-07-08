package me.tbsten.cream.ksp.core.parentOptional

import com.google.devtools.ksp.getAllSuperTypes
import me.tbsten.cream.ksp.core.common.fullName

/**
 * Order [ParentOptionalAccessorSpec.entries] so that an entry whose child is a *strict subtype*
 * of another entry's child always comes first ("most derived first"), keeping discovery order
 * between unrelated entries — a stable topological order, so generation stays deterministic.
 *
 * The generated `when` tries its `is` branches top to bottom: with `Leaf : Middle` both
 * contributing to the same accessor, emitting `is Middle` before `is Leaf` would shadow the
 * `Leaf` branch and return the wrong property for `Leaf` instances at runtime.
 */
internal fun ParentOptionalAccessorSpec.withEntriesMostDerivedFirst(): ParentOptionalAccessorSpec {
    if (entries.size <= 1) return this

    // Resolved once per distinct child: getAllSuperTypes resolves the whole supertype graph.
    val superTypeNamesByChild: Map<String, Set<String>> =
        entries
            .map { it.child }
            .distinctBy { it.fullName }
            .associate { child ->
                child.fullName to
                    child
                        .getAllSuperTypes()
                        .mapNotNull { it.declaration.qualifiedName?.asString() }
                        .toSet()
            }

    fun ParentOptionalEntry.isStrictSubtypeOf(other: ParentOptionalEntry): Boolean =
        child.fullName != other.child.fullName &&
            other.child.fullName in superTypeNamesByChild[child.fullName].orEmpty()

    val remaining = entries.toMutableList()
    val ordered = ArrayList<ParentOptionalEntry>(entries.size)
    while (remaining.isNotEmpty()) {
        // The first (discovery order) entry with no remaining strict subtype: every more-derived
        // entry has already been emitted, so this branch cannot shadow one of theirs.
        val next =
            remaining.firstOrNull { candidate ->
                remaining.none { other -> other !== candidate && other.isStrictSubtypeOf(candidate) }
            }
                // Unreachable (subtyping is acyclic) — defensive so odd resolver models cannot hang.
                ?: remaining.first()
        remaining.remove(next)
        ordered += next
    }
    return copy(entries = ordered)
}
