package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import me.tbsten.cream.ksp.util.ksp.asString

/**
 * Render the type used in an `is <subtype>` when-branch.
 *
 * A subtype's type parameters are only inferable in a bare `is Sub` check when every one of
 * them is pinned by the sealed parent (e.g. `Filled<T> : Box<T>`); Kotlin then narrows
 * `this: Box<T>` to `Sub<T>` for free, so we keep the bare form. When the subtype declares an
 * *extra* parameter the parent does not pin (e.g. `Tagged<T, M> : Box<T>`), a bare
 * `is Box.Tagged` is rejected ("2 type arguments expected"), so we render the pinned
 * parameters with the parent's names and star-project the rest: `is Box.Tagged<T, *>`.
 *
 * When [sealedClass] is not a *direct* supertype of the generic [leaf] (it sits behind an
 * intermediate type, e.g. `Leaf<T> : Mid` with `Mid : Root`), no pinning information is
 * available here and a bare `is Leaf` is likewise rejected ("N type arguments expected"), so
 * every parameter is star-projected: `is Leaf<*>`. The branch only needs the subtype check —
 * generation that reads a property whose type references an unpinned type parameter is
 * rejected up front, so the star projection never degrades an accessed value's type.
 */
internal fun renderWhenBranchType(
    leaf: KSClassDeclaration,
    sealedClass: KSClassDeclaration,
): String {
    if (leaf.typeParameters.isEmpty()) return leaf.fullName

    val parentSuperType =
        leaf.superTypes
            .map { it.resolve() }
            .firstOrNull { it.declaration.fullName == sealedClass.fullName }
            ?: return leaf.starProjectedTypeText()

    val parentParamNames = sealedClass.typeParameters.map { it.name.asString() }
    val leafParamToParentName = HashMap<String, String>()
    parentSuperType.arguments.forEachIndexed { index, arg ->
        val leafParamName = (arg.type?.resolve()?.declaration as? KSTypeParameter)?.name?.asString()
        if (leafParamName != null && index < parentParamNames.size) {
            leafParamToParentName[leafParamName] = parentParamNames[index]
        }
    }

    val allPinned = leaf.typeParameters.all { it.name.asString() in leafParamToParentName }
    if (allPinned) return leaf.fullName

    val args =
        leaf.typeParameters.joinToString(", ") { tp ->
            leafParamToParentName[tp.name.asString()] ?: "*"
        }
    return "${leaf.fullName}<$args>"
}

/** `Leaf<*, *>` — every type parameter star-projected. */
private fun KSClassDeclaration.starProjectedTypeText(): String = "$fullName<${typeParameters.joinToString(", ") { "*" }}>"

internal fun renderTypeParameterList(
    typeParameters: List<KSTypeParameter>,
    omitPackages: List<String>,
): String {
    if (typeParameters.isEmpty()) return ""
    return buildString {
        append("<")
        append(
            typeParameters.joinToString(", ") { tp ->
                buildString {
                    append(tp.name.asString())
                    val bound =
                        tp.bounds
                            .singleOrNull()
                            ?.resolve()
                            ?.asString(omitPackages = omitPackages)
                    if (bound != null && bound != "kotlin.Any?") {
                        append(" : ")
                        append(bound)
                    }
                }
            },
        )
        append(">")
    }
}

/**
 * ` where T : A, T : B` for type parameters carrying *multiple* upper bounds. Kotlin can only
 * express a single bound inline (handled by [renderTypeParameterList]); additional bounds must
 * go in a where-clause, otherwise the generated `fun <T> Sealed<T>` drops them and fails the
 * sealed type's own bound check. Returns "" when no parameter needs one.
 */
internal fun renderWhereClause(
    typeParameters: List<KSTypeParameter>,
    omitPackages: List<String>,
): String {
    val entries =
        typeParameters.flatMap { tp ->
            val bounds = tp.bounds.toList()
            if (bounds.size <= 1) {
                emptyList()
            } else {
                bounds.map { "${tp.name.asString()} : ${it.resolve().asString(omitPackages = omitPackages)}" }
            }
        }
    return if (entries.isEmpty()) "" else " where ${entries.joinToString(", ")}"
}

internal fun renderSealedReceiverType(
    sealedClass: KSClassDeclaration,
    omitPackages: List<String>,
): String =
    buildString {
        append(sealedClass.fullName)
        if (sealedClass.typeParameters.isNotEmpty()) {
            append("<")
            append(sealedClass.typeParameters.joinToString(", ") { it.name.asString() })
            append(">")
        }
        // omitPackages is reserved for future trimming; current rendering always uses fullName
        // to stay safe across packages and nested types.
        @Suppress("UNUSED_EXPRESSION")
        omitPackages
    }
