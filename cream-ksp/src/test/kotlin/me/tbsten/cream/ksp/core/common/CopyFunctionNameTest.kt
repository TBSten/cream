package me.tbsten.cream.ksp.core.common

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.ksp.options.ClassDeclarationInfo
import me.tbsten.cream.ksp.options.CopyFunNamingStrategy
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.EscapeDot

private fun classInfo(
    packageName: String,
    underPackageName: String,
): ClassDeclarationInfo =
    object : ClassDeclarationInfo {
        override val packageName = packageName
        override val underPackageName = underPackageName
        override val simpleName = underPackageName.substringAfterLast(".")
        override val fullName = if (packageName.isEmpty()) underPackageName else "$packageName.$underPackageName"
    }

private fun options(
    strategy: CopyFunNamingStrategy,
    escapeDot: EscapeDot,
): CreamOptions =
    CreamOptions(
        copyFunNamePrefix = "copyTo",
        copyFunNamingStrategy = strategy,
        escapeDot = escapeDot,
        notCopyToObject = false,
    )

private fun name(
    source: ClassDeclarationInfo,
    target: ClassDeclarationInfo,
    options: CreamOptions,
): String = copyFunctionName(source, target, options).toString()

// Source/target pairs exercising both a flat (package-only) target and nested (dotted) targets.
private val source = classInfo("me.tbsten.example.source", "Source")
private val target = classInfo("me.tbsten.example.target", "Target")
private val state = classInfo("me.tbsten.example", "State")
private val stateSuccess = classInfo("me.tbsten.example", "State.Success")
private val stateSuccessMoreLoading = classInfo("me.tbsten.example", "State.Success.MoreLoading")

internal class CopyFunctionNameTest :
    FunSpec({
        test("under-package, lower-camel-case") {
            val o = options(CopyFunNamingStrategy.`under-package`, EscapeDot.`lower-camel-case`)
            name(source, target, o) shouldBe "copyToTarget"
            name(state, stateSuccess, o) shouldBe "copyToStateSuccess"
        }

        test("diff, lower-camel-case") {
            val o = options(CopyFunNamingStrategy.`diff`, EscapeDot.`lower-camel-case`)
            name(source, target, o) shouldBe "copyToTargetTarget"
            name(state, stateSuccess, o) shouldBe "copyToSuccess"
        }

        test("simple-name, lower-camel-case") {
            val o = options(CopyFunNamingStrategy.`simple-name`, EscapeDot.`lower-camel-case`)
            name(source, target, o) shouldBe "copyToTarget"
            name(state, stateSuccess, o) shouldBe "copyToSuccess"
        }

        test("full-name, lower-camel-case") {
            val o = options(CopyFunNamingStrategy.`full-name`, EscapeDot.`lower-camel-case`)
            name(source, target, o) shouldBe "copyToMeTbstenExampleTargetTarget"
            name(state, stateSuccess, o) shouldBe "copyToMeTbstenExampleStateSuccess"
        }

        test("inner-name, lower-camel-case") {
            val o = options(CopyFunNamingStrategy.`inner-name`, EscapeDot.`lower-camel-case`)
            name(source, target, o) shouldBe "copyToTarget"
            name(state, stateSuccess, o) shouldBe "copyToSuccess"
            name(state, stateSuccessMoreLoading, o) shouldBe "copyToSuccessMoreLoading"
        }

        test("under-package, replace-to-underscore") {
            val o = options(CopyFunNamingStrategy.`under-package`, EscapeDot.`replace-to-underscore`)
            name(source, target, o) shouldBe "copyTo_Target"
            name(state, stateSuccess, o) shouldBe "copyTo_State_Success"
        }

        test("diff, replace-to-underscore") {
            val o = options(CopyFunNamingStrategy.`diff`, EscapeDot.`replace-to-underscore`)
            name(source, target, o) shouldBe "copyTo_target_Target"
            name(state, stateSuccess, o) shouldBe "copyTo_Success"
        }

        test("simple-name, replace-to-underscore") {
            val o = options(CopyFunNamingStrategy.`simple-name`, EscapeDot.`replace-to-underscore`)
            name(source, target, o) shouldBe "copyTo_Target"
            name(state, stateSuccess, o) shouldBe "copyTo_Success"
        }

        test("full-name, replace-to-underscore") {
            val o = options(CopyFunNamingStrategy.`full-name`, EscapeDot.`replace-to-underscore`)
            name(source, target, o) shouldBe "copyTo_me_tbsten_example_target_Target"
            name(state, stateSuccess, o) shouldBe "copyTo_me_tbsten_example_State_Success"
        }

        test("inner-name, replace-to-underscore") {
            val o = options(CopyFunNamingStrategy.`inner-name`, EscapeDot.`replace-to-underscore`)
            name(source, target, o) shouldBe "copyTo_Target"
            name(state, stateSuccess, o) shouldBe "copyTo_Success"
            name(state, stateSuccessMoreLoading, o) shouldBe "copyTo_Success_MoreLoading"
        }
    })
