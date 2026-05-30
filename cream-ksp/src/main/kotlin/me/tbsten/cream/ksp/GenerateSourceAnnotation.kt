package me.tbsten.cream.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import kotlin.reflect.KClass

internal sealed interface GenerateSourceAnnotation<A : Annotation> {
    val annotationClass: KClass<A>
    val annotationTarget: KSDeclaration

    /** User-provided KDoc description (raw, before [trimIndent]). Empty when absent. */
    val kdocDescription: String

    /** User-provided KDoc examples (raw, before [trimIndent]). Empty when absent. */
    val kdocExamples: List<String>

    data class CopyFrom(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyFrom> {
        override val annotationClass =
            me.tbsten.cream.CopyFrom::class
    }

    data class CopyTo(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyTo> {
        override val annotationClass: KClass<me.tbsten.cream.CopyTo> =
            me.tbsten.cream.CopyTo::class
    }

    data class CopyToChildren(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyToChildren> {
        override val annotationClass: KClass<me.tbsten.cream.CopyToChildren> =
            me.tbsten.cream.CopyToChildren::class
    }

    data class SealedCopy(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.SealedCopy> {
        override val annotationClass: KClass<me.tbsten.cream.SealedCopy> =
            me.tbsten.cream.SealedCopy::class
    }

    data class CombineTo(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineTo> {
        override val annotationClass: KClass<me.tbsten.cream.CombineTo> =
            me.tbsten.cream.CombineTo::class
    }

    data class CombineFrom(
        override val annotationTarget: KSDeclaration,
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineFrom> {
        override val annotationClass: KClass<me.tbsten.cream.CombineFrom> =
            me.tbsten.cream.CombineFrom::class
    }

    data class CopyMapping(
        override val annotationTarget: KSDeclaration,
        val propertyMappings: List<Pair<String, String>> = emptyList(),
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyMapping> {
        override val annotationClass: KClass<me.tbsten.cream.CopyMapping> =
            me.tbsten.cream.CopyMapping::class
    }

    data class CombineMapping(
        override val annotationTarget: KSDeclaration,
        val propertyMappings: List<Pair<String, String>> = emptyList(),
        override val kdocDescription: String = "",
        override val kdocExamples: List<String> = emptyList(),
    ) : GenerateSourceAnnotation<me.tbsten.cream.CombineMapping> {
        override val annotationClass: KClass<me.tbsten.cream.CombineMapping> =
            me.tbsten.cream.CombineMapping::class
    }
}
