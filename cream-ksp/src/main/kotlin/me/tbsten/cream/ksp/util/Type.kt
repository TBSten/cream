package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Variance
import me.tbsten.cream.ksp.UnknownCreamException

internal val KSType.asString: String
    get() = buildString {
        append(
            declaration.qualifiedName?.asString()
                ?: throw UnknownCreamException("declaration.qualifiedName is null")
        )

        // type parameters
        if (arguments.isNotEmpty()) {
            append("<")

            append(
                arguments.joinToString(", ") { typeArg ->
                    // type variance
                    when (typeArg.variance) {
                        Variance.STAR -> "*"
                        Variance.INVARIANT,
                        Variance.COVARIANT,
                        Variance.CONTRAVARIANT,
                            -> {
                            if (typeArg.variance != Variance.INVARIANT) {
                                append(typeArg.variance.label)
                                append(" ")
                            }
                            typeArg.type!!.resolve().asString
                        }
                    }
                }
            )
            append(">")
        }

        if (isMarkedNullable) append("?")
    }
