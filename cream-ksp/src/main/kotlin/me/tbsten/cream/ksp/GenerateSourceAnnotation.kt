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
}
