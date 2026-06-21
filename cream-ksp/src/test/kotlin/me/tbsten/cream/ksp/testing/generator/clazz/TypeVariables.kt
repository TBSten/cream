package me.tbsten.cream.ksp.testing.generator.clazz

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Declares the [TypeVariableName]s referenced anywhere in [memberTypes] on this builder, deduped by
 * name and kept in first-seen order. A no-op when no type variable is referenced.
 */
internal fun TypeSpec.Builder.addDerivedTypeVariables(memberTypes: Iterable<TypeName>): TypeSpec.Builder {
    val collected = LinkedHashMap<String, TypeVariableName>()
    memberTypes.forEach { it.collectTypeVariablesInto(collected) }
    return addTypeVariables(collected.values)
}

private fun TypeName.collectTypeVariablesInto(acc: LinkedHashMap<String, TypeVariableName>) {
    when (this) {
        is TypeVariableName ->
            if (name !in acc) {
                acc[name] = this
                bounds.forEach { it.collectTypeVariablesInto(acc) }
            }

        is ParameterizedTypeName -> typeArguments.forEach { it.collectTypeVariablesInto(acc) }

        is WildcardTypeName -> {
            inTypes.forEach { it.collectTypeVariablesInto(acc) }
            outTypes.forEach { it.collectTypeVariablesInto(acc) }
        }

        is LambdaTypeName -> {
            receiver?.collectTypeVariablesInto(acc)
            parameters.forEach { it.type.collectTypeVariablesInto(acc) }
            returnType.collectTypeVariablesInto(acc)
        }

        else -> Unit
    }
}
