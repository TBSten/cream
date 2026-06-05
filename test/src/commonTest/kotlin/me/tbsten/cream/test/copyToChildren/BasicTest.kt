package me.tbsten.cream.test.copyToChildren

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FunSpec({
        test("parentToChildren") {
            val parent: Parent = ChildDataObject

            mapOf(
                parent.copyToChildDataObject() to ChildDataObject,
                parent.copyToChildDataClass(
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
            val parent: Parent = ChildDataObject

            mapOf(
                parent.copyToChildDataObject() to ChildDataObject,
                parent.copyToChildDataClass(
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
            val parent: Parent = ChildDataObject

            mapOf(
                parent.copyToGrandChildDataObject() to GrandChildDataObject,
                parent.copyToGrandChildDataClass(
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

        // Copy functions are generated only on the annotated class (Parent), so reaching a
        // grandchild leaf from a grandchild-level value still resolves to the Parent extension.
        // Because the receiver is typed as Parent, intermediate (child-level) properties are not
        // carried over automatically and must be supplied explicitly.
        test("grandChildLevelValueToGrandChildLeafViaAnnotatedClassReceiver") {
            val grandChild: GrandChildSealedInterface = GreatGrandChildDataObject

            mapOf(
                grandChild.copyToGrandChildDataObject() to GrandChildDataObject,
                grandChild.copyToGrandChildDataClass(
                    childProp1 = 1.0,
                    childProp2 = 2L,
                    childProp3 = "child",
                    grandChildProp1 = "grandChild",
                    grandChildProp2 = 123,
                    grandChildProp3 = true,
                ) to
                    GrandChildDataClass(
                        parentProp1 = grandChild.parentProp1,
                        parentProp2 = grandChild.parentProp2,
                        parentProp3 = grandChild.parentProp3,
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
