package me.tbsten.cream.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import kotlin.reflect.KClass

internal sealed interface GenerateSourceAnnotation<A : Annotation> {
    val annotationClass: KClass<A>
    val annotationTarget: KSClassDeclaration

    data class CopyFrom(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyFrom> {
        override val annotationClass =
            me.tbsten.cream.CopyFrom::class
    }

    data class CopyTo(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyTo> {
        override val annotationClass: KClass<me.tbsten.cream.CopyTo> =
            me.tbsten.cream.CopyTo::class
    }

    data class CopyToChildren(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.CopyToChildren> {
        override val annotationClass: KClass<me.tbsten.cream.CopyToChildren> =
            me.tbsten.cream.CopyToChildren::class
    }

    data class MutableCopyTo(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.MutableCopyTo> {
        override val annotationClass: KClass<me.tbsten.cream.MutableCopyTo> =
            me.tbsten.cream.MutableCopyTo::class
    }

    data class MutableCopyFrom(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.MutableCopyFrom> {
        override val annotationClass: KClass<me.tbsten.cream.MutableCopyFrom> =
            me.tbsten.cream.MutableCopyFrom::class
    }

    data class MutableCopyToChildren(
        override val annotationTarget: KSClassDeclaration,
    ) : GenerateSourceAnnotation<me.tbsten.cream.MutableCopyToChildren> {
        override val annotationClass: KClass<me.tbsten.cream.MutableCopyToChildren> =
            me.tbsten.cream.MutableCopyToChildren::class
    }
}
