package me.tbsten.cream.ksp.util

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import me.tbsten.cream.ksp.InvalidCreamUsageException

/**
 * Why a class declaration cannot be used as the *target* of a generated copy / combine function.
 *
 * cream generates `Target(prop = ...)` to build the target, so a valid target must be a concrete
 * class (or object) whose primary constructor the generated code can call. `@CopyTo` / `@CopyFrom`
 * additionally accept a sealed interface, which fans out to its concrete subclasses. The
 * dispatcher ([me.tbsten.cream.ksp.transform.appendCopyFunction]) decides which rejection applies
 * per [com.google.devtools.ksp.symbol.ClassKind]; this enum is the single source of truth for the
 * reason text so it can be snapshot-tested in isolation.
 *
 * Each constant carries the human-facing [message] / [solution] (`%s` is replaced with the target
 * name); [asException] wraps them in the standard [InvalidCreamUsageException] envelope so the
 * text matches every other cream usage error.
 */
internal enum class CopyTargetRejection(
    val message: String,
    val solution: String,
) {
    SEALED_CLASS(
        message = "Unsupported target sealed class (%s). A sealed class cannot be instantiated directly.",
        solution = "Specify one of its concrete subclasses as the target.",
    ),
    ABSTRACT_CLASS(
        message = "Unsupported target abstract class (%s). An abstract class cannot be instantiated.",
        solution = "Specify a concrete (non-abstract) class as the target.",
    ),
    INNER_CLASS(
        message = "Unsupported target inner class (%s). An inner class requires an enclosing instance and cannot be a target.",
        solution = "Make %s a top-level or nested (non-inner) class.",
    ),
    NON_SEALED_INTERFACE(
        message = "Unsupported target interface (%s). It must be a sealed interface.",
        solution = "Please make %s a sealed interface.",
    ),
    ANNOTATION_CLASS(
        message = "Unsupported target annotation class (%s). An annotation class cannot be used as a target.",
        solution = "Specify a class, object, or sealed interface as the target.",
    ),
    ENUM_CLASS(
        message = "Unsupported target enum class (%s). An enum entry cannot be constructed as a target.",
        solution = "Specify a class, object, annotation class, or sealed interface as the target.",
    ),
    PRIVATE_CONSTRUCTOR(
        message = "Unsupported target %s: its primary constructor is private and cannot be called from generated code.",
        solution = "Make the primary constructor of %s public or internal.",
    ),
    PROTECTED_CONSTRUCTOR(
        message = "Unsupported target %s: its primary constructor is protected and cannot be called from generated code.",
        solution = "Make the primary constructor of %s public or internal.",
    ),
    ;

    fun asException(targetFullName: String): InvalidCreamUsageException =
        InvalidCreamUsageException(
            message = message.replace("%s", targetFullName),
            solution = solution.replace("%s", targetFullName),
        )
}

/**
 * Returns the [CopyTargetRejection] explaining why this `class` declaration cannot be a copy /
 * combine target, or `null` when it is usable (a concrete class whose primary constructor is
 * reachable from generated code).
 *
 * Checks the most specific reason first: `sealed` (cannot be instantiated) before the generic
 * `abstract` case, then `inner`, and finally the primary constructor visibility for an otherwise
 * concrete class. Only applies to `ClassKind.CLASS`; other kinds (interface / enum / object /
 * annotation class) are routed by the dispatcher.
 */
internal fun KSClassDeclaration.concreteClassRejection(): CopyTargetRejection? =
    when {
        isSealed() -> CopyTargetRejection.SEALED_CLASS
        isAbstract() -> CopyTargetRejection.ABSTRACT_CLASS
        modifiers.contains(Modifier.INNER) -> CopyTargetRejection.INNER_CLASS
        else -> constructorRejection()
    }

/** Reject classes whose primary constructor cannot be invoked from generated (external) code. */
private fun KSClassDeclaration.constructorRejection(): CopyTargetRejection? =
    when (primaryConstructor?.getVisibility()) {
        Visibility.PRIVATE -> CopyTargetRejection.PRIVATE_CONSTRUCTOR
        Visibility.PROTECTED -> CopyTargetRejection.PROTECTED_CONSTRUCTOR
        Visibility.PUBLIC,
        Visibility.INTERNAL,
        Visibility.JAVA_PACKAGE,
        Visibility.LOCAL,
        null,
        -> null
    }
