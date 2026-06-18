package me.tbsten.cream.ksp

import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.DefaultCopyFunctionName
import kotlin.reflect.KClass

internal sealed interface GenerateSourceAnnotation<A : Annotation> {
    val annotation: A
    val annotationClass: KClass<A>

    /** User-provided KDoc description (raw, before [trimIndent]). Empty when absent. */
    val kdocDescription: String

    /** User-provided KDoc examples (raw, before [trimIndent]). Empty when absent. */
    val kdocExamples: List<String>

    /** Visibility modifier for the generated function. Defaults to [CopyVisibility.INHERIT]. */
    val visibility: CopyVisibility

    /** Function name template for the generated function. Defaults to [DefaultCopyFunctionName]. */
    val funNameTemplate: String

    data class CopyFrom(
        override val annotation: me.tbsten.cream.CopyFrom,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyFrom> {
        override val annotationClass = me.tbsten.cream.CopyFrom::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = runCatching { annotation.visibility }.getOrDefault(CopyVisibility.INHERIT)
        override val funNameTemplate: String get() = runCatching { annotation.funName }.getOrDefault(DefaultCopyFunctionName)
    }

    data class CopyTo(
        override val annotation: me.tbsten.cream.CopyTo,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyTo> {
        override val annotationClass = me.tbsten.cream.CopyTo::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = runCatching { annotation.visibility }.getOrDefault(CopyVisibility.INHERIT)
        override val funNameTemplate: String get() = runCatching { annotation.funName }.getOrDefault(DefaultCopyFunctionName)
    }

    data class CopyToChildren(
        override val annotation: me.tbsten.cream.CopyToChildren,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyToChildren> {
        override val annotationClass = me.tbsten.cream.CopyToChildren::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = runCatching { annotation.visibility }.getOrDefault(CopyVisibility.INHERIT)

        // CopyToChildren has no funName property; use the default.
        override val funNameTemplate: String get() = DefaultCopyFunctionName
    }

    /**
     * [me.tbsten.cream.SealedCopy] is [Repeatable] and the typed-proxy getter for any field that
     * was not given explicitly throws [NoSuchElementException] on AA-backed KSP2.  To avoid that,
     * values are read from the raw [com.google.devtools.ksp.symbol.KSAnnotation] in the feature
     * processor and passed as constructor parameters here.  The [annotation] field holds the typed
     * proxy purely to satisfy the sealed interface contract; processors must not let GSA read from
     * it through the interface getters.
     */
    data class SealedCopy(
        override val annotation: me.tbsten.cream.SealedCopy,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
        override val visibility: CopyVisibility = CopyVisibility.INHERIT,
        override val funNameTemplate: String = DefaultCopyFunctionName,
    ) : GenerateSourceAnnotation<me.tbsten.cream.SealedCopy> {
        override val annotationClass = me.tbsten.cream.SealedCopy::class
    }

    data class CombineTo(
        override val annotation: me.tbsten.cream.CombineTo,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineTo> {
        override val annotationClass = me.tbsten.cream.CombineTo::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = runCatching { annotation.visibility }.getOrDefault(CopyVisibility.INHERIT)
        override val funNameTemplate: String get() = runCatching { annotation.funName }.getOrDefault(DefaultCopyFunctionName)
    }

    /**
     * [me.tbsten.cream.CombineFrom] is [Repeatable]: multiple annotations on the same class are
     * merged into **one** generated function and the effective [funNameTemplate] is resolved by
     * comparing all occurrences (conflict detection in the feature processor).  Because of this
     * multi-occurrence merge, and to guard against AA-backed KSP2 absent-field issues, the
     * resolved values are passed as explicit constructor parameters rather than being read from
     * the typed proxy's getters.  [annotation] satisfies the sealed interface contract.
     */
    data class CombineFrom(
        override val annotation: me.tbsten.cream.CombineFrom,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
        override val visibility: CopyVisibility = CopyVisibility.INHERIT,
        override val funNameTemplate: String = DefaultCopyFunctionName,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineFrom> {
        override val annotationClass = me.tbsten.cream.CombineFrom::class
    }

    /**
     * [me.tbsten.cream.CopyMapping] does not carry a visibility property; [visibility] is always
     * [CopyVisibility.INHERIT].  Property name remappings are passed separately as [propertyMappings]
     * (already resolved to `List<Pair<String,String>>` by the feature processor) rather than
     * re-reading them from the typed annotation proxy here, to avoid KSP2 AA-backend issues with
     * `Array<AnnotationClass>` parameters.
     */
    data class CopyMapping(
        override val annotation: me.tbsten.cream.CopyMapping,
        val propertyMappings: List<Pair<String, String>> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyMapping> {
        override val annotationClass = me.tbsten.cream.CopyMapping::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = CopyVisibility.INHERIT
        override val funNameTemplate: String get() = runCatching { annotation.funName }.getOrDefault(DefaultCopyFunctionName)
    }

    /**
     * Property name remappings are passed separately as [propertyMappings] (already resolved to
     * `List<Pair<String,String>>` by the feature processor) for the same reason as [CopyMapping].
     */
    data class CombineMapping(
        override val annotation: me.tbsten.cream.CombineMapping,
        val propertyMappings: List<Pair<String, String>> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineMapping> {
        override val annotationClass = me.tbsten.cream.CombineMapping::class
        override val kdocDescription: String get() = runCatching { annotation.kdoc.description }.getOrDefault("")
        override val kdocExamples: List<String> get() = runCatching { annotation.kdoc.examples.toList() }.getOrDefault(emptyList())
        override val visibility: CopyVisibility get() = runCatching { annotation.visibility }.getOrDefault(CopyVisibility.INHERIT)
        override val funNameTemplate: String get() = runCatching { annotation.funName }.getOrDefault(DefaultCopyFunctionName)
    }
}
