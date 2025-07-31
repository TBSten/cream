package me.tbsten.cream.test.copyToChildren

import me.tbsten.cream.CopyToChildren

@CopyToChildren
sealed interface Parent {
    val parentProp1: String
    val parentProp2: Int
    val parentProp3: Boolean
}


// Child
data object ChildDataObject : Parent {
    override val parentProp1 = "aaa"
    override val parentProp2 = 1
    override val parentProp3 = true
}

data class ChildDataClass(
    override val parentProp1: String,
    override val parentProp2: Int,
    override val parentProp3: Boolean,
    val childProp1: Double,
    val childProp2: Long,
    val childProp3: String,
) : Parent

sealed interface ChildSealedInterface : Parent {
    val childProp1: Double
    val childProp2: Long
    val childProp3: String
}


// GrandChild
data object GrandChildDataObject : ChildSealedInterface {
    override val parentProp1 = "bbb"
    override val parentProp2 = 2
    override val parentProp3 = false
    override val childProp1 = 1.23
    override val childProp2 = 100L
    override val childProp3 = "ccc"
}

data class GrandChildDataClass(
    override val parentProp1: String,
    override val parentProp2: Int,
    override val parentProp3: Boolean,
    override val childProp1: Double,
    override val childProp2: Long,
    override val childProp3: String,
    val grandChildProp1: String,
    val grandChildProp2: Int,
    val grandChildProp3: Boolean,
) : ChildSealedInterface

sealed interface GrandChildSealedInterface : ChildSealedInterface {
    val grandChildProp1: String
    val grandChildProp2: Int
    val grandChildProp3: Boolean
}


// GreatGrandChild
data object GreatGrandChildDataObject : GrandChildSealedInterface {
    override val parentProp1 = "ddd"
    override val parentProp2 = 3
    override val parentProp3 = true
    override val childProp1 = 2.34
    override val childProp2 = 200L
    override val childProp3 = "eee"
    override val grandChildProp1 = "fff"
    override val grandChildProp2 = 999
    override val grandChildProp3 = false
}

data class GreatGrandChildDataClass(
    override val parentProp1: String,
    override val parentProp2: Int,
    override val parentProp3: Boolean,
    override val childProp1: Double,
    override val childProp2: Long,
    override val childProp3: String,
    override val grandChildProp1: String,
    override val grandChildProp2: Int,
    override val grandChildProp3: Boolean,
    val greatGrandChildProp1: List<out Any>,
    val greatGrandChildProp2: Double,
    val greatGrandChildProp3: Boolean,
) : GrandChildSealedInterface

// sealed interface GreatGrandChildSealedInterface : GrandChildSealedInterface {
//     val greatGrandChildProp1: List<String>
//     val greatGrandChildProp2: Double
//     val greatGrandChildProp3: Boolean
// }
