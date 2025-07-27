package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import me.tbsten.cream.CopyFrom
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.UnknownCreamException
import me.tbsten.cream.ksp.util.asString

internal fun getCopyFunctionTypeParameters(
    sourceClass: KSClassDeclaration,
    targetConstructor: KSFunctionDeclaration,
) = CopyFunctionTypeParameters(
    sourceClass = sourceClass,
    targetConstructor = targetConstructor,
)

internal class CopyFunctionTypeParameters(
    private val sourceClass: KSClassDeclaration,
    private val targetConstructor: KSFunctionDeclaration,
) : Map<String, CopyFunctionTypeParameter> by copyFunctionTypeParametersMap(
    sourceClass,
    targetConstructor
) {
    fun getNameFromSourceClassTypeParameters(
        typeParameter: KSTypeParameter,
    ): String? {
        return this.entries.find {
            it.value.source?.name?.asString() == typeParameter.name.asString()
        }?.key
    }

    fun getNameFromTargetConstructorTypeParameters(
        typeParameter: KSTypeParameter,
    ): String? {
        return this.entries.find {
            it.value.target?.name?.asString() == typeParameter.name.asString()
        }?.key

    }
}

private fun copyFunctionTypeParametersMap(
    sourceClass: KSClassDeclaration,
    targetConstructor: KSFunctionDeclaration,
) = buildMap<String, CopyFunctionTypeParameter> {
    targetConstructor.typeParameters.forEach { typeParameter ->
        val name =
            typeParameter
                .getAnnotationsByType(CopyTo.Map::class)
                .firstOrNull()
                ?.value
                ?: typeParameter.name.asString()
        val prevTypeParameter = get(name)
        val newTypeParameter =
            prevTypeParameter?.copy(
                target = typeParameter,
            ) ?: CopyFunctionTypeParameter(
                source = null,
                target = typeParameter,
            )

        put(
            key = newTypeParameter.name,
            value = newTypeParameter,
        )
    }

    sourceClass.typeParameters.forEach { typeParameter ->
        val name =
            typeParameter
                .getAnnotationsByType(CopyFrom.Map::class)
                .firstOrNull()
                ?.value
                ?: typeParameter.name.asString()
        val prevTypeParameter = get(name)

        put(
            key = name,
            value = prevTypeParameter?.copy(
                source = typeParameter,
            ) ?: CopyFunctionTypeParameter(
                source = typeParameter,
                target = null,
            ),
        )
    }
}

internal data class CopyFunctionTypeParameter(
    val source: KSTypeParameter?,
    val target: KSTypeParameter?,
) {
    val bounds: Sequence<KSTypeReference>
        get() {
            val sourceBounds = source?.bounds ?: emptySequence()
            val targetBounds = target?.bounds ?: emptySequence()
            return (sourceBounds + targetBounds).distinctBy { it.resolve().asString() }
        }

    val name: String
        get() =
            target?.name?.asString()
                ?: source?.name?.asString()
                ?: throw UnknownCreamException(
                    message = "Invalid copy function type parameter: both source and target are null.",
                )
}
