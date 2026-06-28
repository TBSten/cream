package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.core.common.annotationsOf

/**
 * A concrete leaf of a `@SealedCopy` hierarchy, classified purely by whether it is **copyable** —
 * whether there is a `copy`-shaped function we can delegate to. See [classify].
 */
internal sealed interface SealedCopyLeaf {
    val declaration: KSClassDeclaration

    data class Copyable(
        override val declaration: KSClassDeclaration,
        val funName: String,
    ) : SealedCopyLeaf

    data class NonCopyable(
        override val declaration: KSClassDeclaration,
    ) : SealedCopyLeaf
}

internal fun KSClassDeclaration.collectAbstractProperties(): List<KSPropertyDeclaration> =
    getAllProperties()
        .filter { it.isAbstract() }
        .toList()

/**
 * A leaf is classified purely by whether it is **copyable** — whether there is a
 * `copy`-shaped function we can delegate to. The subtype's *kind* (object / data class /
 * plain class) is not a category in its own right; it only feeds the single
 * copyable-or-not question answered by [hasDelegatableCopy].
 *
 * A subtype can also point cream at an explicit delegate by annotating that function with
 * [SealedCopy.Map]; when present, the annotated function's own name is used.
 */
internal fun KSClassDeclaration.classify(abstractProperties: List<KSPropertyDeclaration>): SealedCopyLeaf {
    val mappedFunName =
        getAllFunctions()
            .firstOrNull { it.annotationsOf(SealedCopy.Map::class).any() }
            ?.simpleName
            ?.asString()
    return when {
        mappedFunName != null -> SealedCopyLeaf.Copyable(this, mappedFunName)
        hasDelegatableCopy(abstractProperties) -> SealedCopyLeaf.Copyable(this, "copy")
        else -> SealedCopyLeaf.NonCopyable(this)
    }
}

/**
 * Whether this leaf exposes a default `copy(...)` to delegate to (consulted only when the
 * subtype does not mark an explicit delegate with [SealedCopy.Map]):
 *  - an `object` is a singleton with nothing to copy → never copyable
 *  - a `data class` has a synthetic `copy(...)` that KSP does not surface, so we trust it;
 *    the Kotlin compiler still rejects the generated source if that synthetic shape
 *    diverges from the abstract properties, giving a clear use-site diagnostic
 *  - otherwise a `copy(...)` must actually be declared and accept every abstract property
 */
private fun KSClassDeclaration.hasDelegatableCopy(abstractProperties: List<KSPropertyDeclaration>): Boolean {
    if (classKind == ClassKind.OBJECT) return false
    if (modifiers.contains(Modifier.DATA) && classKind == ClassKind.CLASS) return true
    return findCompatibleCopyFunction("copy", abstractProperties) != null
}

private fun KSClassDeclaration.findCompatibleCopyFunction(
    funName: String,
    abstractProperties: List<KSPropertyDeclaration>,
): KSFunctionDeclaration? =
    getAllFunctions()
        .firstOrNull { func ->
            if (func.simpleName.asString() != funName) return@firstOrNull false
            abstractProperties.all { absProp ->
                val propName = absProp.simpleName.asString()
                val propType = absProp.type.resolve()
                func.parameters.any { param ->
                    param.name?.asString() == propName &&
                        isParameterAssignableFromProperty(propType, param.type.resolve())
                }
            }
        }

private fun isParameterAssignableFromProperty(
    propertyType: KSType,
    parameterType: KSType,
): Boolean {
    if (parameterType.isAssignableFrom(propertyType)) return true
    // Cover the generic case where both sides are unresolved type parameters
    // sharing the same name (e.g. Result<T> → Success<T>'s ctor takes T).
    val propDecl = propertyType.declaration
    val paramDecl = parameterType.declaration
    if (propDecl is KSTypeParameter && paramDecl is KSTypeParameter) {
        return propDecl.name.asString() == paramDecl.name.asString()
    }
    return false
}
