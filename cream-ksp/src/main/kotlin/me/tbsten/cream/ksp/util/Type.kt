package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance

internal val KSType.asString: String
    get() = buildString {
        append(
            declaration.qualifiedName?.asString()
                ?: error("KSTypeReference does not have a qualified name: $this")
        )

        // type parameters
        if (arguments.isNotEmpty()) {
            append("<")
            arguments.forEach { typeArg ->
                // type variance
                when (typeArg.variance) {
                    Variance.STAR -> append("*")
                    Variance.INVARIANT,
                    Variance.COVARIANT,
                    Variance.CONTRAVARIANT -> {
                        if (typeArg.variance != Variance.INVARIANT) {
                            append(typeArg.variance.label)
                            append(" ")
                        }
                        append(typeArg.type!!.resolve().asString)
                    }
                }

            }
            append(">")
        }
    }
