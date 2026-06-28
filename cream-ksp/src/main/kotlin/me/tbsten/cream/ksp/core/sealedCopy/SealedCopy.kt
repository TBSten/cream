package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import me.tbsten.cream.CopyVisibility
import me.tbsten.cream.NonCopyableStrategy
import me.tbsten.cream.SealedCopy
import me.tbsten.cream.ksp.core.common.GenerateSourceAnnotation
import me.tbsten.cream.ksp.core.common.annotationsOf
import me.tbsten.cream.ksp.core.common.toModifierString
import me.tbsten.cream.ksp.util.ksp.asString
import me.tbsten.cream.ksp.util.ksp.collectConcreteSubclasses
import java.io.BufferedWriter

/**
 * Append a single `<sealedType>.<funName>(...)` extension that returns the sealed parent
 * type itself (or its nullable form), dispatching to each leaf subclass's `copy(...)`.
 *
 * Behaviour follows the contract documented on [me.tbsten.cream.SealedCopy]. The leaf analysis
 * lives in [SealedCopyLeaf.kt][classify], type-text rendering in
 * [SealedCopyTypeRendering.kt][renderSealedReceiverType], KDoc in [appendSealedCopyKDoc] and the
 * non-copyable diagnostic in [nonCopyableErrorException].
 */
context(logger: KSPLogger)
internal fun BufferedWriter.appendSealedCopyFunction(
    sealedClass: KSClassDeclaration,
    funName: String,
    nonCopyableStrategy: NonCopyableStrategy,
    omitPackages: List<String>,
    generateSourceAnnotation: GenerateSourceAnnotation.SealedCopy,
) {
    val abstractProperties = sealedClass.collectAbstractProperties()
    val classifiedLeaves =
        sealedClass
            .collectConcreteSubclasses()
            .map { it.classify(abstractProperties) }
            .toList()
    val nonCopyableLeaves = classifiedLeaves.filterIsInstance<SealedCopyLeaf.NonCopyable>()

    if (nonCopyableStrategy == NonCopyableStrategy.ERROR && nonCopyableLeaves.isNotEmpty()) {
        val exception = nonCopyableErrorException(sealedClass, nonCopyableLeaves, funName)
        // Report a clean positioned COMPILATION_ERROR and emit nothing for this function so the
        // transactional writer leaves no partial file behind.
        logger.error(exception.message.orEmpty(), sealedClass)
        return
    }

    val nullable = nonCopyableStrategy == NonCopyableStrategy.RETURN_NULL && nonCopyableLeaves.isNotEmpty()
    val returnTypeText = renderSealedReceiverType(sealedClass, omitPackages) + if (nullable) "?" else ""

    // A param is REQUIRED when it does NOT receive a `= this.x` default in the emitted header,
    // i.e. the abstract property is marked @SealedCopy.Exclude.
    val requiredParamNames =
        abstractProperties
            .filter { it.annotationsOf(SealedCopy.Exclude::class).any() }
            .map { it.simpleName.asString() }

    appendSealedCopyKDoc(sealedClass, funName, classifiedLeaves, nullable, generateSourceAnnotation, requiredParamNames)
    appendSealedCopyHeader(sealedClass, funName, returnTypeText, abstractProperties, omitPackages, generateSourceAnnotation.visibility)
    appendSealedCopyBody(sealedClass, classifiedLeaves, abstractProperties, nonCopyableStrategy)
}

private fun BufferedWriter.appendSealedCopyHeader(
    sealedClass: KSClassDeclaration,
    funName: String,
    returnTypeText: String,
    abstractProperties: List<KSPropertyDeclaration>,
    omitPackages: List<String>,
    visibility: CopyVisibility,
) {
    append(visibility.toModifierString(sealedClass))
    append(" fun ")
    append(renderTypeParameterList(sealedClass.typeParameters, omitPackages))
    if (sealedClass.typeParameters.isNotEmpty()) append(" ")
    append(renderSealedReceiverType(sealedClass, omitPackages))
    append(".")
    append(funName)
    append("(")
    appendLine()
    abstractProperties.forEach { prop ->
        val typeText = prop.type.resolve().asString(omitPackages = omitPackages)
        val propName = prop.simpleName.asString()
        val excluded = prop.annotationsOf(SealedCopy.Exclude::class).any()
        if (excluded) {
            appendLine("    $propName: $typeText,")
        } else {
            appendLine("    $propName: $typeText = this.$propName,")
        }
    }
    append("): ")
    append(returnTypeText)
    append(renderWhereClause(sealedClass.typeParameters, omitPackages))
    append(" = when (this) {")
    appendLine()
}

private fun BufferedWriter.appendSealedCopyBody(
    sealedClass: KSClassDeclaration,
    classifiedLeaves: List<SealedCopyLeaf>,
    abstractProperties: List<KSPropertyDeclaration>,
    nonCopyableStrategy: NonCopyableStrategy,
) {
    classifiedLeaves.forEach { leaf ->
        when (leaf) {
            is SealedCopyLeaf.Copyable -> {
                val args =
                    abstractProperties.joinToString(", ") { prop ->
                        val name = prop.simpleName.asString()
                        "$name = $name"
                    }
                appendLine("    is ${renderWhenBranchType(leaf.declaration, sealedClass)} -> this.${leaf.funName}($args)")
            }

            is SealedCopyLeaf.NonCopyable -> {
                val whenBranchValue =
                    when (nonCopyableStrategy) {
                        NonCopyableStrategy.RETURN_AS_IS -> "this"
                        NonCopyableStrategy.RETURN_NULL -> "null"
                        // ERROR has already been handled above; emit RETURN_AS_IS as a defensive default.
                        NonCopyableStrategy.ERROR -> "this"
                    }
                appendLine("    is ${renderWhenBranchType(leaf.declaration, sealedClass)} -> $whenBranchValue")
            }
        }
    }
    appendLine("}")
    appendLine()
}
