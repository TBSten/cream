package me.tbsten.cream.ksp.core.sealedCopy

import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter

/**
 * Whether a delegate parameter of type [parameterType] can accept the value of an abstract
 * property of type [propertyType]. Shared by leaf classification ([classify]) and `@SealedCopy.Via`
 * validation ([collectSealedCopyViaErrors]) so both answer the question identically.
 */
internal fun isParameterAssignableFromProperty(
    propertyType: KSType,
    parameterType: KSType,
): Boolean {
    if (parameterType.isAssignableFrom(propertyType)) return true
    // Cover the generic case where both sides are unresolved type parameters
    // sharing the same name (e.g. Result<T> → Success<T>'s ctor takes T).
    val propDecl = propertyType.declaration
    val paramDecl = parameterType.declaration
    if (propDecl is KSTypeParameter && paramDecl is KSTypeParameter) {
        return propDecl.name.asString() == paramDecl.name.asString()
    }
    return false
}
