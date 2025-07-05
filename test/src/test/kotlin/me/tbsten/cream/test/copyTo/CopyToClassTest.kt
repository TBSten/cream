package me.tbsten.cream.test.copyTo

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyToClassTest {
    @Test
    fun parentTo() {
        val parent: Parent = ChildDataObject

        mapOf(
            parent.copyToChildDataObject() to ChildDataObject,
            parent.copyToChildDataClass(
                childProp1 = 1.23,
                childProp2 = 12345L,
                childProp3 = "childProp3-value",
            ) to ChildDataClass(
                parentProp1 = parent.parentProp1,
                parentProp2 = parent.parentProp2,
                parentProp3 = parent.parentProp3,
                childProp1 = 1.23,
                childProp2 = 12345L,
                childProp3 = "childProp3-value",
            )
        ).forEach { (actual, expected) ->
            assertEquals(actual, expected)
        }
    }

    @Test
    fun childDataObjectTo() {
        val childDataObject: ChildDataObject = ChildDataObject

        mapOf(
            childDataObject.copyToChildDataClass(
                childProp1 = 2.3,
                childProp2 = 9750,
                childProp3 = "childProp3-value"
            ) to ChildDataClass(
                parentProp1 = childDataObject.parentProp1,
                parentProp2 = childDataObject.parentProp2,
                parentProp3 = childDataObject.parentProp3,
                childProp1 = 2.3,
                childProp2 = 9750,
                childProp3 = "childProp3-value",
            )
        ).forEach { (actual, expected) ->
            assertEquals(actual, expected)
        }
    }
}
