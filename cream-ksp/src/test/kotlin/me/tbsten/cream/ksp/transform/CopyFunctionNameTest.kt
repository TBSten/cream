package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import me.tbsten.cream.ksp.options.CopyFunNamingStrategy
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.EscapeDot

internal class CopyFunctionNameTest :
    FunSpec({
        test("under-package, lower-camel-case") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`under-package`,
                    escapeDot = EscapeDot.`lower-camel-case`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyToTarget",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyToStateSuccess",
                ),
            )
        }

        test("diff-parent, lower-camel-case") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`diff`,
                    escapeDot = EscapeDot.`lower-camel-case`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration(
                        "me.tbsten.example.source",
                        "Source",
                    ),
                    mockKSClassDeclaration(
                        "me.tbsten.example.target",
                        "Target",
                    ),
                    "copyToTargetTarget",
                ),
                Triple(
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State",
                    ),
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State.Success",
                    ),
                    "copyToSuccess",
                ),
            )
        }

        test("simple-name, lower-camel-case") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`simple-name`,
                    escapeDot = EscapeDot.`lower-camel-case`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration(
                        "me.tbsten.example.source",
                        "Source",
                    ),
                    mockKSClassDeclaration(
                        "me.tbsten.example.target",
                        "Target",
                    ),
                    "copyToTarget",
                ),
                Triple(
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State",
                    ),
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State.Success",
                    ),
                    "copyToSuccess",
                ),
            )
        }

        test("full-name, lower-camel-case") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`full-name`,
                    escapeDot = EscapeDot.`lower-camel-case`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration(
                        "me.tbsten.example.target",
                        "Target",
                    ),
                    "copyToMeTbstenExampleTargetTarget",
                ),
                Triple(
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State",
                    ),
                    mockKSClassDeclaration(
                        "me.tbsten.example",
                        "State.Success",
                    ),
                    "copyToMeTbstenExampleStateSuccess",
                ),
            )
        }

        test("inner-name, lower-camel-case") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`inner-name`,
                    escapeDot = EscapeDot.`lower-camel-case`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyToTarget",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyToSuccess",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success.MoreLoading"),
                    "copyToSuccessMoreLoading",
                ),
            )
        }

        test("under-package, replace-to-underscore") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`under-package`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyTo_Target",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyTo_State_Success",
                ),
            )
        }

        test("diff-parent, replace-to-underscore") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`diff`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyTo_target_Target",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyTo_Success",
                ),
            )
        }

        test("simple-name, replace-to-underscore") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`simple-name`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyTo_Target",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyTo_Success",
                ),
            )
        }

        test("full-name, replace-to-underscore") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`full-name`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyTo_me_tbsten_example_target_Target",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyTo_me_tbsten_example_State_Success",
                ),
            )
        }

        test("inner-name, replace-to-underscore") {
            testCopyFunctionName(
                CreamOptions(
                    copyFunNamePrefix = "copyTo",
                    copyFunNamingStrategy = CopyFunNamingStrategy.`inner-name`,
                    escapeDot = EscapeDot.`replace-to-underscore`,
                    notCopyToObject = false,
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example.source", "Source"),
                    mockKSClassDeclaration("me.tbsten.example.target", "Target"),
                    "copyTo_Target",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success"),
                    "copyTo_Success",
                ),
                Triple(
                    mockKSClassDeclaration("me.tbsten.example", "State"),
                    mockKSClassDeclaration("me.tbsten.example", "State.Success.MoreLoading"),
                    "copyTo_Success_MoreLoading",
                ),
            )
        }
    })

private fun testCopyFunctionName(
    options: CreamOptions,
    vararg triples: Triple<KSClassDeclaration, KSClassDeclaration, String>,
) {
    triples.forEach { (source, target, expectedFunName) ->
        val resultFunName = copyFunctionName(source, target, options).toString()

        resultFunName shouldBe expectedFunName
    }
}

private fun mockKSClassDeclaration(
    dummyPackage: String,
    underPackage: String,
): KSClassDeclaration =
    mockk<KSClassDeclaration> {
        val dummyQualifiedName =
            buildString {
                append(dummyPackage)
                if (dummyPackage.isNotEmpty()) append(".")
                append(underPackage)
            }
        every { qualifiedName } returns
            mockk<KSName> {
                every { asString() } returns dummyQualifiedName
            }
        every { packageName } returns
            mockk<KSName> {
                every { asString() } returns dummyPackage
            }
        every { simpleName } returns
            mockk<KSName> {
                every { asString() } returns dummyQualifiedName.split(".").last()
            }
    }
