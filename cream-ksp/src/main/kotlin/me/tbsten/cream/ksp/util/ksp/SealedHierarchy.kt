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
