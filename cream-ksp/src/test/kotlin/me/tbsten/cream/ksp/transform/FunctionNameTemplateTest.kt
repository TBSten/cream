package me.tbsten.cream.ksp.transform

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import me.tbsten.cream.CopyTargetFullName
import me.tbsten.cream.CopyTargetInnerName
import me.tbsten.cream.CopyTargetSimpleName
import me.tbsten.cream.CopyTargetUnderPackage
import me.tbsten.cream.DefaultCopyFunctionName
import me.tbsten.cream.copy_target_full_name
import me.tbsten.cream.copy_target_inner_name
import me.tbsten.cream.copy_target_simple_name
import me.tbsten.cream.copy_target_under_package
import me.tbsten.cream.ksp.core.common.containsAnyCopyFunNameToken
import me.tbsten.cream.ksp.core.common.resolveFunNameTemplate
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

// target = com.example.UiState.Success (a nested class), matching the design's worked example.
private val source = classInfo("com.example", "Source")
private val target = classInfo("com.example", "UiState.Success")

private fun resolve(
    template: String,
    options: CreamOptions = CreamOptions.default,
) = resolveFunNameTemplate(template, source, target, options)

internal class FunctionNameTemplateTest :
    FunSpec({
        test("DefaultCopyFunctionName expands to cream's derived name") {
            resolve(DefaultCopyFunctionName) shouldBe "copyToUiStateSuccess"
        }

        test("Pascal tokens upper-case each dotted segment") {
            resolve(CopyTargetSimpleName) shouldBe "Success"
            resolve(CopyTargetUnderPackage) shouldBe "UiStateSuccess"
            resolve(CopyTargetInnerName) shouldBe "Success"
            resolve(CopyTargetFullName) shouldBe "ComExampleUiStateSuccess"
        }

        test("snake tokens lower-case each whole dotted segment") {
            resolve(copy_target_simple_name) shouldBe "success"
            resolve(copy_target_under_package) shouldBe "uistate_success"
            resolve(copy_target_inner_name) shouldBe "success"
            resolve(copy_target_full_name) shouldBe "com_example_uistate_success"
        }

        test("tokens can be composed with literal prefixes and suffixes") {
            resolve("to" + CopyTargetSimpleName) shouldBe "toSuccess"
            resolve("to_" + copy_target_under_package) shouldBe "to_uistate_success"
            resolve(DefaultCopyFunctionName + "OrNull") shouldBe "copyToUiStateSuccessOrNull"
        }

        test("multiple, mid-template, and repeated tokens all expand") {
            resolve(CopyTargetUnderPackage + "From" + CopyTargetSimpleName) shouldBe "UiStateSuccessFromSuccess"
            resolve("x" + CopyTargetSimpleName + "y") shouldBe "xSuccessy"
            resolve(CopyTargetSimpleName + "_" + copy_target_simple_name) shouldBe "Success_success"
            resolve(CopyTargetSimpleName + CopyTargetSimpleName) shouldBe "SuccessSuccess"
        }

        test("a template with no token is returned verbatim") {
            resolve("toState") shouldBe "toState"
        }

        test("CopyTarget tokens ignore the project naming/escape options but Default follows them") {
            val nonDefault =
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`simple-name`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                )
            // fixed rendering, unaffected by options
            resolve(CopyTargetUnderPackage, nonDefault) shouldBe "UiStateSuccess"
            resolve(copy_target_under_package, nonDefault) shouldBe "uistate_success"
            // the derived default does follow the options
            resolve(DefaultCopyFunctionName, nonDefault) shouldBe "copyTo_Success"
        }

        test("containsAnyCopyFunNameToken detects tokens") {
            containsAnyCopyFunNameToken("to" + CopyTargetSimpleName) shouldBe true
            containsAnyCopyFunNameToken(DefaultCopyFunctionName) shouldBe true
            containsAnyCopyFunNameToken("toState") shouldBe false
            containsAnyCopyFunNameToken("") shouldBe false
        }
    })
