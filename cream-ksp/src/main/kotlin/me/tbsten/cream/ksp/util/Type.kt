package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.Variance

internal fun KSType.asString(
    typeParameterToString: (KSType) -> String = { it.declaration.simpleName.asString() },
): String =
    buildString {
        val type = this@asString
        append(
            when (type.declaration) {
//                is KSTypeParameter -> type.declaration.simpleName.asString()
                is KSTypeParameter -> typeParameterToString(type)
                else -> type.declaration.fullName
            }
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
                            typeArg.type!!.resolve().asString(
                                typeParameterToString = typeParameterToString,
                            )
                        }
                    }
                }
            )
            append(">")
        }

        if (isMarkedNullable) append("?")
    }