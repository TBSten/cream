package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * A concrete leaf of a `@SealedCopy` hierarchy, classified purely by whether it is **copyable** —
 * whether there is a `copy`-shaped function we can delegate to. See [classify].
 */
internal sealed interface SealedCopyLeaf {
    val declaration: KSClassDeclaration

    data class Copyable(
        override val declaration: KSClassDeclaration,
        val funName: String,
        /**
         * Arguments to pass at the delegate call site, one per delegate parameter that is supplied by an
         * abstract property. Each binding renders as `parameterName = abstractPropertyName` — the left side is
         * the delegate's own parameter name, the right side is the generated `copy()` parameter (which is named
         * after the abstract property). For the default `copy` path this is the identity over the abstract
         * properties (`x = x`); for a [SealedCopy.Via] delegate it reflects the delegate's parameter names and
         * any [SealedCopy.Map] renames, and omits defaulted parameters that do not map to an abstract property.
         */
        val argumentBindings: List<ArgumentBinding>,
    ) : SealedCopyLeaf

    data class NonCopyable(
        override val declaration: KSClassDeclaration,
    ) : SealedCopyLeaf
}

/** One argument of a delegate call: `parameterName = abstractPropertyName`. */
internal data class ArgumentBinding(
    val parameterName: String,
    val abstractPropertyName: String,
)

internal fun KSClassDeclaration.collectAbstractProperties(): List<KSPropertyDeclaration> =
    getAllProperties()
        .filter { it.isAbstract() }
        .toList()

/** Functions on this subtype marked with [SealedCopy.Via] (the explicit delegate marker). */
internal fun KSClassDeclaration.viaFunctions(): Sequence<KSFunctionDeclaration> =
    getAllFunctions()
        .filter { it.annotationsOf(SealedCopy.Via::class).any() }

/** The abstract property this parameter binds to: its [SealedCopy.Map] `value`, or the parameter name. */
internal fun KSValueParameter.mappedAbstractPropertyName(): String? {
    val paramName = name?.asString() ?: return null
    return annotationsOf(SealedCopy.Map::class)
        .firstOrNull()
        ?.getArgument<String>("value")
        ?: paramName
}

/**
 * A leaf is classified purely by whether it is **copyable** — whether there is a
 * `copy`-shaped function we can delegate to. The subtype's *kind* (object / data class /
 * plain class) is not a category in its own right; it only feeds the single
 * copyable-or-not question answered by [hasDelegatableCopy].
 *
 * A subtype can also point cream at an explicit delegate by annotating that function with
 * [SealedCopy.Via]; when present, the annotated function's own name is used and each of its
 * parameters is bound to an abstract property (by name or via [SealedCopy.Map]). The delegate
 * is validated separately (see [collectSealedCopyViaErrors]); [classify] trusts that validation ran and
 * simply builds the argument bindings from the first `@Via` function.
 */
internal fun KSClassDeclaration.classify(abstractProperties: List<KSPropertyDeclaration>): SealedCopyLeaf {
    val viaFunction = viaFunctions().firstOrNull()
    return when {
        viaFunction != null ->
            SealedCopyLeaf.Copyable(
                declaration = this,
                funName = viaFunction.simpleName.asString(),
                argumentBindings = viaFunction.toArgumentBindings(abstractProperties),
            )

        hasDelegatableCopy(abstractProperties) ->
            SealedCopyLeaf.Copyable(
                declaration = this,
                funName = "copy",
                argumentBindings = abstractProperties.identityBindings(),
            )

        else -> SealedCopyLeaf.NonCopyable(this)
    }
}

/**
 * Build the delegate call arguments for a [SealedCopy.Via] function: for each parameter that resolves to an
 * abstract property (by name, or by [SealedCopy.Map]'s `value`), emit `parameterName = abstractPropertyName`.
 * Parameters that do not map to an abstract property are omitted — validation guarantees such parameters have
 * a default value, so the call still compiles.
 */
private fun KSFunctionDeclaration.toArgumentBindings(abstractProperties: List<KSPropertyDeclaration>): List<ArgumentBinding> {
    val abstractNames = abstractProperties.map { it.simpleName.asString() }.toSet()
    return parameters.mapNotNull { param ->
        val paramName = param.name?.asString() ?: return@mapNotNull null
        val abstractName = param.mappedAbstractPropertyName() ?: return@mapNotNull null
        if (abstractName in abstractNames) ArgumentBinding(paramName, abstractName) else null
    }
}

/** The default `copy` delegate accepts every abstract property under its own name: `x = x`. */
private fun List<KSPropertyDeclaration>.identityBindings(): List<ArgumentBinding> =
    map { prop ->
        val name = prop.simpleName.asString()
        ArgumentBinding(parameterName = name, abstractPropertyName = name)
    }

/**
 * Whether this leaf exposes a default `copy(...)` to delegate to (consulted only when the
 * subtype does not mark an explicit delegate with [SealedCopy.Via]):
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

internal fun isParameterAssignableFromProperty(
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
