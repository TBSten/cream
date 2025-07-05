package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test

class CopyToChildrenTest {
    @Test
    fun parentToChildren() {
        val parent: Parent = ChildDataObject
        parent.copyToChildDataObject()
        parent.copyToChildDataClass(
            childProp1 = 1.0,
            childProp2 = 2L,
            childProp3 = "child"
        )
        parent.copyToGrandChildDataObject()
        parent.copyToGrandChildDataClass(
            childProp1 = 1.0,
            childProp2 = 2L,
            childProp3 = "child",
            grandChildProp1 = "grandChild",
            grandChildProp2 = 3,
            grandChildProp3 = true,
        )
    }
}
