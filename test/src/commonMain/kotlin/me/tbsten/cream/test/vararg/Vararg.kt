package me.tbsten.cream.test.vararg

import me.tbsten.cream.CopyTo

// Regular (non-data) classes: `data class` forbids `vararg val` properties, so vararg copy
// targets are exercised here with plain classes (issue #49).
@CopyTo(VarargTarget::class)
class VarargSource(
    val id: Int,
    vararg val tags: String,
)

class VarargTarget(
    val id: Int,
    vararg val tags: String,
)

@CopyTo(PrimitiveVarargTarget::class)
class PrimitiveVarargSource(
    val id: Int,
    vararg val nums: Int,
)

class PrimitiveVarargTarget(
    val id: Int,
    vararg val nums: Int,
)

@CopyTo(ArrayToVarargTarget::class)
class ArrayToVarargSource(
    val id: Int,
    val tags: Array<String>,
)

class ArrayToVarargTarget(
    val id: Int,
    vararg val tags: String,
)
