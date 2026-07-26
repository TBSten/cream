package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import me.tbsten.cream.CopyVisibility
import kotlin.reflect.KClass

/**
 * Identifies the source annotation that triggered a generation and exposes everything cream needs
 * while emitting the function: the user-facing metadata (KDoc / visibility / funName template) and
 * the annotation-scoped generation rules (`.Map` resolution, `@Exclude` handling, `object` targets).
 *
 * Every metadata value is derived from the raw [KSAnnotation] via the core/common extract helpers
 * ([extractKDoc] / [copyVisibilityArgument] / [funNameTemplate]). Reading from the raw annotation
 * — rather than a typed `getAnnotationsByType` proxy — is intentional:
 *  - the AA-backed KSP2 proxy throws `NoSuchElementException` for a field left at its default,
 *    whereas the raw `arguments` list simply omits it and the helpers fall back to the documented
 *    defaults (so no `runCatching` guard is needed), and
 *  - it lets a feature processor hand each instance the *specific* occurrence it is generating for,
 *    which a single typed proxy cannot express for a `@Repeatable` annotation.
 *
 * This interface is deliberately NOT sealed: the generation rules below are resolved by polymorphic
 * dispatch rather than by an exhaustive `when`, so an implementation defined outside this package
 * plugs into the same generators. Every rule has a "does nothing special" default, so an
 * implementation only overrides the ones its annotation actually needs.
 *
 * cream's own eight implementations live next to this file: [CopyToSourceAnnotation] /
 * [CopyFromSourceAnnotation] / [CopyToChildrenSourceAnnotation] / [SealedCopySourceAnnotation] in
 * `CopySourceAnnotation.kt`, [CombineToSourceAnnotation] / [CombineFromSourceAnnotation] in
 * `CombineSourceAnnotation.kt`, and [CopyMappingSourceAnnotation] /
 * [CombineMappingSourceAnnotation] in `MappingSourceAnnotation.kt`.
 */
internal interface GenerateSourceAnnotation {
    /** Raw annotation occurrence this generation was triggered by. */
    val annotation: KSAnnotation

    /** Simple name of the annotation, e.g. `"CopyTo"`; used in the generated KDoc. */
    val annotationSimpleName: String get() = annotation.shortName.asString()

    /**
     * The declaration the triggering [annotation] is attached to: the source for source-side
     * annotations (`@CopyTo` / `@CopyToChildren`) and the target/holder for target-side ones
     * (`@CopyFrom` / `@CopyMapping`). Generated KDoc attributes the function to this declaration
     * (issue #144), so it is read straight from the annotation's enclosing node instead of being
     * threaded through every generator as a separate parameter.
     */
    val annotatedDeclaration: KSDeclaration
        get() =
            annotation.parent as? KSDeclaration
                ?: error("@$annotationSimpleName is not attached to a declaration")

    /** User-provided KDoc description (`kdoc.description`); empty when absent. */
    val kdocDescription: String get() = annotation.extractKDoc().first

    /** User-provided KDoc examples (`kdoc.examples`); empty when absent. */
    val kdocExamples: List<String> get() = annotation.extractKDoc().second

    /** Visibility modifier for the generated function; [CopyVisibility.INHERIT] when absent. */
    val visibility: CopyVisibility get() = annotation.copyVisibilityArgument()

    /** Function-name template for the generated function; the derived default when absent. */
    val funNameTemplate: String get() = annotation.funNameTemplate()

    /**
     * Resolve the SOURCE property that supplies [parameter]'s auto-copy default through this
     * annotation's own `.Map` semantics, or `null` when it names no mapping for [parameter].
     *
     * Returning `null` is the normal case: [findMatchedProperty] then falls back to the shared
     * plain name match, which every annotation gets for free. Only the explicit remapping is
     * annotation-scoped, so a `.Map` belonging to one annotation never affects functions generated
     * by another.
     *
     * [parameterName] is [parameter]'s name, already resolved by the caller (a parameter without
     * one never reaches here).
     */
    fun findMappedSourceProperty(
        parameter: KSValueParameter,
        source: KSClassDeclaration,
        parameterName: String,
    ): KSPropertyDeclaration? = null

    /**
     * Whether the auto-copy default matched for [parameter] should be suppressed — this
     * annotation's own `@Exclude` semantics.
     *
     * [matchedProperty] is the source property [findMatchedProperty] resolved (`null` when nothing
     * matched) and [matchedSource] the class it came from. Keeping this annotation-scoped is what
     * stops e.g. `@SealedCopy.Exclude` and `@CopyToChildren.Exclude` from suppressing each other's
     * parameters when both annotations coexist on the same sealed parent.
     */
    fun isExcluded(
        parameter: KSValueParameter,
        matchedProperty: KSPropertyDeclaration?,
        matchedSource: KSClassDeclaration,
    ): Boolean = false

    /**
     * The TARGET-side `@Exclude` annotation (placed on a constructor parameter) whose ineffective
     * use this annotation reports via [warnIfTargetExcludeHasNoEffect]. `null` — the default —
     * means no such warning is emitted for this annotation.
     */
    val warnedTargetExcludeAnnotation: KClass<out Annotation>? get() = null

    /**
     * The SOURCE-side `@Exclude` annotation (placed on a source property) whose ineffective use
     * this annotation reports via [warnIfSourceExcludeHasNoEffect]. `null` — the default — means no
     * such warning is emitted for this annotation, either because it is not source-side or because
     * the warning is emitted elsewhere.
     */
    val warnedSourceExcludeAnnotation: KClass<out Annotation>? get() = null

    /**
     * Whether an `object` target is skipped instead of getting a copy function that returns the
     * singleton. [notCopyToObjectOption] is the `cream.notCopyToObject` build option, offered so an
     * annotation that exposes its own control can defer to it when the user leaves that control
     * unset.
     *
     * Defaults to `false`: an annotation that names its (possibly `object`) target explicitly
     * always generates.
     */
    fun skipsObjectTarget(notCopyToObjectOption: Boolean): Boolean = false
}
