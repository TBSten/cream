package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier.SEALED
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios

private val copyTargetSimpleName = MemberName("me.tbsten.cream", "CopyTargetSimpleName")
private val defaultCopyFunctionName = MemberName("me.tbsten.cream", "DefaultCopyFunctionName")

private fun literal(name: String): CodeBlock = CodeBlock.of("%S", name)

private fun prefixedToken(prefix: String): CodeBlock = CodeBlock.of("%S + %M", prefix, copyTargetSimpleName)

private fun suffixedDefault(suffix: String): CodeBlock = CodeBlock.of("%M + %S", defaultCopyFunctionName, suffix)

/** sealed parent with two concrete leaves — fans out to more than one per-leaf copy function. */
private fun twoLeafParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("id")),
        children =
            listOf(
                childClass("Loading", classNameOf("Source"), overrides = listOf(Prop("id"))),
                childClass("Done", classNameOf("Source"), overrides = listOf(Prop("id")), extras = listOf(Prop("data", STRING))),
            ),
    )

/** sealed parent with exactly one concrete leaf — generates a single function, so a plain literal funName is fine. */
private fun singleLeafParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("id")),
        children = listOf(childClass("Only", classNameOf("Source"), overrides = listOf(Prop("id")))),
    )

/** sealed branch (itself sealed) under [parent], holding [leaves]. */
private fun nestedSealedBranch(
    name: String,
    parent: ClassName,
    vararg leaves: TypeSpec,
): TypeSpec =
    TypeSpec
        .interfaceBuilder(name)
        .addModifiers(SEALED)
        .addSuperinterface(parent)
        .apply { leaves.forEach { addType(it) } }
        .build()

/**
 * sealed parent whose only direct child is itself sealed and has a single concrete leaf
 * (Source -> Source.Branch -> Source.Branch.Only). Only one function is generated, so a plain
 * literal funName does NOT collide — even though the direct child is sealed (the case the old
 * per-direct-child check wrongly rejected).
 */
private fun singleTransitiveLeafParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        abstractProps = listOf(Prop("id")),
        children =
            listOf(
                nestedSealedBranch(
                    "Branch",
                    classNameOf("Source"),
                    childClass("Only", classNameOf("Source", "Branch"), overrides = listOf(Prop("id"))),
                ),
            ),
    )

/** sealed parent with one data leaf and one object leaf; notCopyToObject suppresses the object leaf. */
private fun dataPlusObjectParent(): TypeSpec =
    sealedInterfaceParent(
        "Source",
        children =
            listOf(
                childClass("DataChild", classNameOf("Source"), extras = listOf(Prop("a"))),
                objectChild("ObjectChild", classNameOf("Source")),
            ),
    )

internal fun funNameScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // A per-target token renames every leaf's function uniquely (intoLoading / intoDone).
        "tokenFunName" to copyToChildren(twoLeafParent(), funName = prefixedToken("into")),
        // DefaultCopyFunctionName keeps cream's derived per-leaf name and only appends a suffix.
        "defaultSuffixFunName" to copyToChildren(twoLeafParent(), funName = suffixedDefault("OrNull")),
        // A plain literal on a multi-leaf parent would collide across leaves and is rejected.
        "literalFunNameRejected" to copyToChildren(twoLeafParent(), funName = literal("toState")),
        // A plain literal is allowed when only one function is generated (single direct leaf).
        "singleLeafLiteralFunName" to copyToChildren(singleLeafParent(), funName = literal("toState")),
        // A plain literal is allowed when the transitive concrete-leaf set is a single function, even
        // though the direct child is sealed (one sealed child -> one concrete leaf).
        "singleTransitiveLeafLiteralFunName" to copyToChildren(singleTransitiveLeafParent(), funName = literal("toState")),
        // notCopyToObject suppresses the object leaf, leaving a single generated function, so a plain
        // literal funName is fine even though two leaves exist.
        "notCopyToObjectSingleLeafLiteralFunName" to
            copyToChildren(dataPlusObjectParent(), funName = literal("toState"), notCopyToObject = true),
    )
