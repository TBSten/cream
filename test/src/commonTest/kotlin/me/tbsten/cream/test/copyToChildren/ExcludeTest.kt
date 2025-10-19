package me.tbsten.cream.test.copyToChildren

import kotlin.test.Test
import kotlin.test.assertEquals

class ExcludeTest {
    @Test
    fun copyToChildWithExclude() {
        val parent: CopyToChildrenExcludeTestParent =
            CopyToChildrenExcludeTestParent.Child(
                sharedProp = "shared",
                excludedProp = "excluded",
                childProp = "child",
            )

        val result: CopyToChildrenExcludeTestParent.Child =
            parent.copyToCopyToChildrenExcludeTestParentChild(
                excludedProp = "required",
                childProp = "new child",
            )

        // Verify
        val expected =
            CopyToChildrenExcludeTestParent.Child(
                sharedProp = "shared",
                excludedProp = "required",
                childProp = "new child",
            )

        assertEquals(expected, result)
    }

    @Test
    fun copyToChildWithOverride() {
        val parent: CopyToChildrenExcludeTestParent =
            CopyToChildrenExcludeTestParent.Child(
                sharedProp = "shared",
                excludedProp = "excluded",
                childProp = "child",
            )

        val result: CopyToChildrenExcludeTestParent.Child =
            parent.copyToCopyToChildrenExcludeTestParentChild(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                childProp = "new child",
            )

        // Verify
        val expected =
            CopyToChildrenExcludeTestParent.Child(
                sharedProp = "overridden shared",
                excludedProp = "overridden excluded",
                childProp = "new child",
            )

        assertEquals(expected, result)
    }
}
