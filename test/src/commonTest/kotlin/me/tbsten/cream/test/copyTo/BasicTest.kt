package me.tbsten.cream.test.copyTo

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FunSpec({
        test("parentToChildren") {
            val parent: Parent =
                ChildDataObject

            mapOf(
                parent.copyToChildDataObject() to ChildDataObject,
                parent.copyToChildDataClass(
                    parentOrNull = null,
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                ) to
                    ChildDataClass(
                        parentProp1 = parent.parentProp1,
                        parentProp2 = parent.parentProp2,
                        parentProp3 = parent.parentProp3,
                        childProp1 = 1.0,
                        childProp2 = 2L,
                        childProp3 = "child",
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        test("parentToChildrenWithOverrideParentProp") {
            val parent: Parent =
                ChildDataObject

            mapOf(
                parent.copyToChildDataObject() to ChildDataObject,
                parent.copyToChildDataClass(
                    parentOrNull = null,
                    parentProp1 = "parent",
                    parentProp2 = 123,
                    parentProp3 = false,
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                ) to
                    ChildDataClass(
                        parentProp1 = "parent",
                        parentProp2 = 123,
                        parentProp3 = false,
                        childProp1 = 1.0,
                        childProp2 = 2L,
                        childProp3 = "child",
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        test("parentToGrandChildren") {
            val parent: Parent =
                ChildDataObject

            mapOf(
                parent.copyToGrandChildDataObject() to GrandChildDataObject,
                parent.copyToGrandChildDataClass(
                    parentOrNull = null,
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                    grandChildProp1 = "grandChild",
                    grandChildProp2 = 3,
                    grandChildProp3 = true,
                ) to
                    GrandChildDataClass(
                        parentProp1 = parent.parentProp1,
                        parentProp2 = parent.parentProp2,
                        parentProp3 = parent.parentProp3,
                        childProp1 = 1.0,
                        childProp2 = 2L,
                        childProp3 = "child",
                        grandChildProp1 = "grandChild",
                        grandChildProp2 = 3,
                        grandChildProp3 = true,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        test("parentToGrandChildrenWithOverrideParentProp") {
            val parent: Parent = ChildDataObject

            mapOf(
                parent.copyToGrandChildDataObject() to GrandChildDataObject,
                parent.copyToGrandChildDataClass(
                    parentProp1 = "parent",
                    parentProp2 = 123,
                    parentProp3 = false,
                    parentOrNull = null,
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                    grandChildProp1 = "grandChild",
                    grandChildProp2 = 3,
                    grandChildProp3 = true,
                ) to
                    GrandChildDataClass(
                        parentProp1 = "parent",
                        parentProp2 = 123,
                        parentProp3 = false,
                        childProp1 = 1.0,
                        childProp2 = 2L,
                        childProp3 = "child",
                        grandChildProp1 = "grandChild",
                        grandChildProp2 = 3,
                        grandChildProp3 = true,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        test("childToGrandChildren") {
            val child: ChildSealedInterface =
                GrandChildDataObject

            mapOf(
                child.copyToGrandChildDataObject() to GrandChildDataObject,
                child.copyToGrandChildDataClass(
                    parentOrNull = null,
                    grandChildProp1 = "grandChild",
                    grandChildProp2 = 123,
                    grandChildProp3 = true,
                ) to
                    GrandChildDataClass(
                        parentProp1 = child.parentProp1,
                        parentProp2 = child.parentProp2,
                        parentProp3 = child.parentProp3,
                        childProp1 = child.childProp1,
                        childProp2 = child.childProp2,
                        childProp3 = child.childProp3,
                        grandChildProp1 = "grandChild",
                        grandChildProp2 = 123,
                        grandChildProp3 = true,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }

        test("childToGrandChildrenWithOverrideChildProp") {
            val child: ChildSealedInterface = GrandChildDataObject

            mapOf(
                child.copyToGrandChildDataObject() to GrandChildDataObject,
                child.copyToGrandChildDataClass(
                    parentOrNull = null,
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                    grandChildProp1 = "grandChild",
                    grandChildProp2 = 123,
                    grandChildProp3 = true,
                ) to
                    GrandChildDataClass(
                        parentProp1 = child.parentProp1,
                        parentProp2 = child.parentProp2,
                        parentProp3 = child.parentProp3,
                        childProp1 = 1.0,
                        childProp2 = 2L,
                        childProp3 = "child",
                        grandChildProp1 = "grandChild",
                        grandChildProp2 = 123,
                        grandChildProp3 = true,
                    ),
            ).forEach { (actual, expected) ->
                actual shouldBe expected
            }
        }
    })
