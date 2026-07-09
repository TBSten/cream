package me.tbsten.cream.ksp.util.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration

/**
 * Walk the sealed hierarchy down to its concrete leaves (data class / object / non-sealed class),
 * flattening every intermediate sealed node — the transitive analogue of KSP's own direct-children
 * [KSClassDeclaration.getSealedSubclasses]. Lazy, so callers can short-circuit on the first few
 * leaves.
 */
internal fun KSClassDeclaration.collectConcreteSubclasses(): Sequence<KSClassDeclaration> =
    getSealedSubclasses().flatMap { sub ->
        if (sub.isSealed()) {
            sub.collectConcreteSubclasses()
        } else {
            sequenceOf(sub)
        }
    }

/**
 * Walk the sealed hierarchy down and collect every intermediate **sealed** node strictly below
 * this one — exactly the nodes [collectConcreteSubclasses] flattens away — in discovery
 * (depth-first) order. Lazy, like its concrete counterpart.
 */
internal fun KSClassDeclaration.collectIntermediateSealedSubclasses(): Sequence<KSClassDeclaration> =
    getSealedSubclasses().flatMap { sub ->
        if (sub.isSealed()) {
            sequenceOf(sub) + sub.collectIntermediateSealedSubclasses()
        } else {
            emptySequence()
        }
    }

/**
 * Walk the supertype graph upwards and collect every **sealed** class/interface this class
 * (transitively) inherits from, deduplicated, in first-encounter (breadth-first) order — the
 * inverse direction of [collectConcreteSubclasses]. Non-sealed intermediates are traversed but
 * not collected, so a sealed grandparent behind a plain abstract class is still found.
 */
internal fun KSClassDeclaration.collectSealedAncestors(): List<KSClassDeclaration> {
    val seen = mutableSetOf<String>()
    val sealedAncestors = mutableListOf<KSClassDeclaration>()
    val queue = ArrayDeque<KSClassDeclaration>()
    queue += this
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        current.superTypes.forEach { superTypeRef ->
            val superDeclaration = superTypeRef.resolve().declaration as? KSClassDeclaration ?: return@forEach
            val key = superDeclaration.qualifiedName?.asString() ?: return@forEach
            if (!seen.add(key)) return@forEach
            if (superDeclaration.isSealed()) sealedAncestors += superDeclaration
            queue += superDeclaration
        }
    }
    return sealedAncestors
}
