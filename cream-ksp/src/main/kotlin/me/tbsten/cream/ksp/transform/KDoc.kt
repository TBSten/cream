package me.tbsten.cream.ksp.transform

import me.tbsten.cream.ksp.GenerateSourceAnnotation
import me.tbsten.cream.ksp.util.underPackageName

internal fun autoGenerateKDoc(generateSourceAnnotation: GenerateSourceAnnotation<*>): String =
    buildString {
        append("Auto generate by @[")
        append(generateSourceAnnotation.annotationClass.simpleName)
        append("] annotation of [")
        append(generateSourceAnnotation.annotationTarget.underPackageName)
        append("]")
    }
