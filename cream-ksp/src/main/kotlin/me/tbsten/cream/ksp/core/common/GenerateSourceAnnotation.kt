package me.tbsten.cream.ksp.core.common

import com.google.devtools.ksp.symbol.KSAnnotation
import me.tbsten.cream.CopyVisibility

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
     * `@CombineFrom` is `@Repeatable` and all occurrences are currently merged into ONE function,
     * so [funNameTemplate] is the single explicit name agreed across occurrences (resolved with
     * conflict detection in the feature processor) rather than any one occurrence's value — hence
     * it is passed in rather than derived from [annotation]. KDoc / visibility still come from the
     * first occurrence's [annotation]. See #134.
     */
    data class CombineFrom(
        override val annotation: KSAnnotation,
        override val funNameTemplate: String,
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
    }

    /** `@CombineMapping` property remappings. `@CombineMapping` has no reverse direction. */
    data class CombineMapping(
        override val annotation: KSAnnotation,
    ) : GenerateSourceAnnotation {
        val propertyMappings: List<Pair<String, String>>
            get() = annotation.extractPropertyMappings()
    }
}
