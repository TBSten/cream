package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import me.tbsten.cream.CopyVisibility
import kotlin.reflect.KClass

/**
 * Identifies the source annotation that triggered a generation and exposes the user-facing metadata
 * cream needs while emitting the function (KDoc / visibility / funName template), plus the two
 * annotation-scoped rules that are decided once per generated function rather than per parameter:
 * which ineffective `@Exclude` to warn about, and whether an `object` target is skipped.
 *
 * The per-parameter rules — `.Map` resolution and `@Exclude` handling — are deliberately NOT on this
 * interface. They travel as standalone [FindMappedSourceProperty] / [IsExcluded] values, which the
 * `core` generators take as ordinary parameters, so a caller that does not drive generation from an
 * annotation at all can supply its own behaviour without implementing this interface. cream's own
 * implementations expose them as plain properties of the same names.
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
 * This interface is deliberately NOT sealed: the rules below are resolved by polymorphic dispatch
 * rather than by an exhaustive `when`, so an implementation defined outside this package plugs into
 * the same generators. Every rule has a "does nothing special" default, so an implementation only
 * overrides the ones its annotation actually needs.
 *
 * cream's own nine implementations live next to this file: [CopyToSourceAnnotation] /
 * [CopyFromSourceAnnotation] / [CopyToChildrenSourceAnnotation] / [SealedCopySourceAnnotation] in
 * `CopySourceAnnotation.kt`, [CombineToSourceAnnotation] / [CombineFromSourceAnnotation] in
 * `CombineSourceAnnotation.kt`, [CopyMappingSourceAnnotation] /
 * [CombineMappingSourceAnnotation] in `MappingSourceAnnotation.kt`, and
 * [CallFromSourceAnnotation] in `CallFromSourceAnnotation.kt`.
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
