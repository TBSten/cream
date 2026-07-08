package me.tbsten.cream.ksp.core.callFrom

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.ksp.core.common.CopyFunctionTypeParameters
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.deprecatedAnnotation
import me.tbsten.cream.ksp.core.common.findMatchedProperty
import me.tbsten.cream.ksp.core.common.fullName
import me.tbsten.cream.ksp.core.common.getCopyFunctionTypeParameters
import me.tbsten.cream.ksp.core.common.isExcludedFromCopy
import me.tbsten.cream.ksp.util.escapeKotlinIdentifier
import me.tbsten.cream.ksp.util.ksp.asString

/**
 * The generated bridge overload's parameter name for the source argument-holder object:
 * the source class's simple name in lowerCamelCase (e.g. `ProcessDataArgs` -> `processDataArgs`),
 * escaped for keyword collisions. Shared with the feature layer, which rejects the annotation
 * when this name collides with one of the target function's own parameters.
 */
internal fun callFromSourceParamName(source: KSClassDeclaration): String =
    source.simpleName
        .asString()
        .replaceFirstChar { it.lowercaseChar() }
        .escapeKotlinIdentifier()

/**
 * One parameter of the original function, classified for bridge generation:
 *
 * - [matchedProperty] is the source property that supplies the parameter's default, when one
 *   matched by name / `@CallFrom.Map` AND is readable from generated code
 *   ([isAccessibleFromGeneratedBridge] — a `private` / `protected` / foreign-`internal` property
 *   is treated as unmatched, because `<source>.<property>` would not compile).
 * - [isKept] — whether the parameter appears in the bridge signature at all. An unmatched
 *   parameter that has a default value on the original function is OMITTED from the bridge (and
 *   from the forwarding call), so the original default still applies; transcribing it would have
 *   made an optional parameter required, and KSP cannot read the default expression to copy it.
 * - [hasAutoDefault] — whether the bridge emits `= <source>.<property>` for it.
 */
internal class CallFromBridgeParameter(
    val parameter: KSValueParameter,
    val matchedProperty: KSPropertyDeclaration?,
    private val isExcluded: Boolean,
) {
    val isKept: Boolean get() = matchedProperty != null || !parameter.hasDefault
    val hasAutoDefault: Boolean get() = matchedProperty != null && !isExcluded
}

/**
 * Classify every parameter of [targetFunction] against [source]. This list is the single source
 * of truth for the bridge's shape: the generator, the generated KDoc, and the overload-collision
 * check in the feature layer all derive from it.
 */
internal fun callFromBridgeParameters(
    source: KSClassDeclaration,
    targetFunction: KSFunctionDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
): List<CallFromBridgeParameter> =
    targetFunction.parameters.map { parameter ->
        val matched =
            parameter
                .findMatchedProperty(source, generateSourceAnnotation)
                ?.takeIf { it.isAccessibleFromGeneratedBridge() }
        CallFromBridgeParameter(
            parameter = parameter,
            matchedProperty = matched,
            isExcluded =
                matched != null &&
                    parameter.isExcludedFromCopy(matched, source, generateSourceAnnotation),
        )
    }

/**
 * Whether generated top-level code (in the annotated function's package, same module) can read
 * this property:
 *
 * - `public` always works; `internal` works only for properties compiled in this module
 *   (`containingFile != null` — classpath symbols have no containing file); everything else
 *   (`private` / `protected` / Java package-private) cannot be referenced as
 *   `<source>.<property>`.
 * - a property deprecated with `ERROR` / `HIDDEN` cannot be read either — unlike `WARNING`
 *   (which cream suppresses by propagating `@Deprecated` onto the bridge), fatal levels are not
 *   silenced by an enclosing deprecated declaration in K2.
 */
private fun KSPropertyDeclaration.isAccessibleFromGeneratedBridge(): Boolean {
    val visibilityAllows =
        when (getVisibility()) {
            Visibility.PUBLIC -> true
            Visibility.INTERNAL -> containingFile != null
            Visibility.PRIVATE,
            Visibility.PROTECTED,
            Visibility.LOCAL,
            Visibility.JAVA_PACKAGE,
            -> false
        }
    if (!visibilityAllows) return false
    val deprecation = deprecatedAnnotation() ?: return true
    return deprecation.level == DeprecationLevel.WARNING
}

/**
 * A bridge overload's identity for redeclaration checks: the receiver type (extension receiver /
 * member enclosing class, `null` for a plain top-level bridge) and the parameter type list
 * (`vararg `-prefixed where applicable). Type parameters are normalized to positional
 * placeholders (`#TP0`, `#TP1`, ...) so `fun <T> f(x: T)` and `fun <U> f(y: U)` — which kotlinc
 * rejects as conflicting overloads — compare equal. All types are rendered fully qualified
 * (`omitPackages = emptyList()`).
 */
internal data class CallFromBridgeSignature(
    val receiver: String?,
    val parameterTypes: List<String>,
)

/**
 * The signature the generated bridge for ([source], [targetFunction]) will have: the bridge's
 * receiver, then the source parameter type followed by the kept original parameters
 * (see [callFromBridgeParameters] — omitted defaulted parameters do not participate).
 */
internal fun callFromBridgeSignature(
    source: KSClassDeclaration,
    targetFunction: KSFunctionDeclaration,
    generateSourceAnnotation: GenerateSourceAnnotation,
): CallFromBridgeSignature {
    val typeParameters =
        getCopyFunctionTypeParameters(
            sourceClass = source,
            targetConstructor = targetFunction,
        )
    val indexByName = typeParameters.keys.withIndex().associate { (index, name) -> name to index }
    val normalize: (KSType) -> String = { type ->
        val declaration = type.declaration
        val name =
            if (declaration is KSTypeParameter) {
                typeParameters.getNameFromTargetConstructorTypeParameters(declaration)
                    ?: typeParameters.getNameFromSourceClassTypeParameters(declaration)
                    ?: declaration.name.asString()
            } else {
                declaration.simpleName.asString()
            }
        indexByName[name]?.let { "#TP$it" } ?: name
    }

    val sourceType =
        buildString {
            append(source.fullName)
            if (source.typeParameters.isNotEmpty()) {
                append("<")
                append(
                    source.typeParameters.joinToString(", ") { typeParam ->
                        val name =
                            typeParameters.getNameFromSourceClassTypeParameters(typeParam)
                                ?: typeParam.name.asString()
                        indexByName[name]?.let { "#TP$it" } ?: name
                    },
                )
                append(">")
            }
        }

    val keptParameterTypes =
        callFromBridgeParameters(source, targetFunction, generateSourceAnnotation)
            .filter { it.isKept }
            .map { it.parameter.renderForSignature(normalize) }

    return CallFromBridgeSignature(
        receiver =
            targetFunction.parentDeclaration
                ?.fullName
                ?: targetFunction.extensionReceiver
                    ?.resolve()
                    ?.asString(omitPackages = emptyList(), typeParameterToString = normalize),
        parameterTypes = listOf(sourceType) + keptParameterTypes,
    )
}

/**
 * The signature of an already-declared function, rendered the same way as
 * [callFromBridgeSignature] so the two can be compared for redeclaration conflicts. Type
 * parameters are normalized positionally against the function's own type-parameter list.
 */
internal fun KSFunctionDeclaration.existingFunctionSignature(): CallFromBridgeSignature {
    val indexByName =
        typeParameters
            .withIndex()
            .associate { (index, typeParam) -> typeParam.name.asString() to index }
    val normalize: (KSType) -> String = { type ->
        val name = type.declaration.simpleName.asString()
        indexByName[name]?.let { "#TP$it" } ?: name
    }
    return CallFromBridgeSignature(
        receiver =
            parentDeclaration
                ?.fullName
                ?: extensionReceiver
                    ?.resolve()
                    ?.asString(omitPackages = emptyList(), typeParameterToString = normalize),
        parameterTypes = parameters.map { it.renderForSignature(normalize) },
    )
}

private fun KSValueParameter.renderForSignature(normalize: (KSType) -> String): String {
    val rendered =
        type
            .resolve()
            .asString(omitPackages = emptyList(), typeParameterToString = normalize)
    return if (isVararg) "vararg $rendered" else rendered
}
