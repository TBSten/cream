package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Visibility


internal val KSClassDeclaration.visibilityStr: String
    get() = when (getVisibility()) {
        Visibility.PUBLIC -> "public"
        Visibility.PRIVATE -> "private"
        Visibility.PROTECTED -> "protected"
        Visibility.INTERNAL -> "internal"
        Visibility.LOCAL,
        Visibility.JAVA_PACKAGE -> ""
    }
