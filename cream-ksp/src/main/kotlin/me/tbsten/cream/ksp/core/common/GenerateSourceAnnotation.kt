package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSDeclaration
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.ksp.util.ksp.getArgument

/**
 * Identifies the source annotation that triggered a generation and exposes the user-facing
 * metadata (KDoc / visibility / funName template) cream needs while emitting the function.
 *
 * Every value is derived from the raw [KSAnnotation] via the core/common extract helpers
 * ([extractKDoc] / [copyVisibilityArgument] / [funNameTemplate]). Reading from the raw annotation
 * — rather than a typed `getAnnotationsByType` proxy — is intentional:
 *  - the AA-backed KSP2 proxy throws `NoSuchElementException` for a field left at its default,
 *    whereas the raw `arguments` list simply omits it and the helpers fall back to the documented
 *    defaults (so no `runCatching` guard is needed), and
 *  - it lets a feature processor hand each instance the *specific* occurrence it is generating for,
 *    which a single typed proxy cannot express for a `@Repeatable` annotation.
 *
 * `when` over the subtypes must enumerate all branches (no `else`) so a new annotation is caught
 * by the compiler.
 */
internal sealed interface GenerateSourceAnnotation {
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

    data class CopyFrom(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation

    data class CopyTo(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation

    /**
     * `@CopyToChildren` exposes a `notCopyToObject` argument controlling whether object subtypes of
     * the sealed hierarchy get a copy function. `null` means the user left it unset, so the caller
     * falls back to the `cream.notCopyToObject` option.
     */
    data class CopyToChildren(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation {
        val notCopyToObject: Boolean? get() = annotation.notCopyToObject()
    }

    data class SealedCopy(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation

    data class CombineTo(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation

    /**
     * `@CombineFrom` is `@Repeatable`; each occurrence generates its own combine function, so KDoc /
     * visibility / funName all derive from *that* occurrence's raw [annotation] (no cross-occurrence
     * merge).
     */
    data class CombineFrom(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation

    /**
     * `@CopyMapping` property remappings. [reversed] swaps each `(source -> target)` pair for the
     * `canReverse` reverse-direction function, which shares this same [annotation].
     */
    data class CopyMapping(
        override val annotation: KSAnnotation,
        val reversed: Boolean = false,
    ) : GenerateSourceAnnotation {
        val propertyMappings: List<Pair<String, String>>
            get() =
                annotation.extractPropertyMappings().let { pairs ->
                    if (reversed) pairs.map { (source, target) -> target to source } else pairs
                }

        /** Generated (target-side) parameter names whose auto-copy default is dropped (`excludes`). */
        val excludedParameterNames: List<String>
            get() = annotation.excludedParameterNames()
    }

    /** `@CombineMapping` property remappings. `@CombineMapping` has no reverse direction. */
    data class CombineMapping(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation {
        val propertyMappings: List<Pair<String, String>>
            get() = annotation.extractPropertyMappings()

        /** Generated (target-side) parameter names whose auto-copy default is dropped (`excludes`). */
        val excludedParameterNames: List<String>
            get() = annotation.excludedParameterNames()
    }
}

/** Read the `excludes: Array<String>` argument of a `@CopyMapping` / `@CombineMapping` as a list of names. */
private fun KSAnnotation.excludedParameterNames(): List<String> = getArgument<List<*>>("excludes")?.filterIsInstance<String>() ?: emptyList()
