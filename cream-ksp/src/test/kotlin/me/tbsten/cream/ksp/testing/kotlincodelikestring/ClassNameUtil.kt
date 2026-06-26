package me.tbsten.cream.ksp.testing.kotlincodelikestring

import kotlin.reflect.jvm.jvmName

val Any.underPackageClassName: String
    get() =
        this::class
            .qualifiedName
            ?.replace("${this.javaClass.`package`?.name}.", "")
            ?: this::class.simpleName
            ?: this::class.jvmName
