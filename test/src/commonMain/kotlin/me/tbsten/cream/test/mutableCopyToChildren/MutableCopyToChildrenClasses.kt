package me.tbsten.cream.test.mutableCopyToChildren

import me.tbsten.cream.MutableCopyToChildren

@MutableCopyToChildren
sealed interface MutableParent {
    val parentProp1: String
    val parentProp2: Int
    val parentProp3: Boolean
}

// Child
data object MutableChildDataObject : MutableParent {
    override val parentProp1 = "aaa"
    override val parentProp2 = 1
    override val parentProp3 = true
}

data class MutableChildDataClass(
    override var parentProp1: String,
    override var parentProp2: Int,
    override var parentProp3: Boolean,
    var childProp1: Double,
    var childProp2: Long,
    var childProp3: String,
) : MutableParent

sealed interface MutableChildSealedInterface : MutableParent {
    override var parentProp1: String
    override var parentProp2: Int
    override var parentProp3: Boolean
    var childProp1: Double
    var childProp2: Long
    var childProp3: String
}

// GrandChild
data object MutableGrandChildDataObject : MutableChildSealedInterface {
    override var parentProp1: String = "bbb"
    override var parentProp2: Int = 2
    override var parentProp3: Boolean = false
    override var childProp1: Double = 1.23
    override var childProp2: Long = 100L
    override var childProp3: String = "ccc"
}

data class MutableGrandChildDataClass(
    override var parentProp1: String,
    override var parentProp2: Int,
    override var parentProp3: Boolean,
    override var childProp1: Double,
    override var childProp2: Long,
    override var childProp3: String,
    var grandChildProp1: String,
    var grandChildProp2: Int,
    var grandChildProp3: Boolean,
) : MutableChildSealedInterface

@MutableCopyToChildren
sealed interface CustomPrefixParent {
    var customProp: String
}

data class CustomPrefixChild(
    override var customProp: String,
    var extraProp: Int,
) : CustomPrefixParent
