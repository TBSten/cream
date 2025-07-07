package me.tbsten.cream.ksp.transform

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import io.mockk.every
import io.mockk.mockk
import me.tbsten.cream.ksp.options.CopyFunNamingStrategy
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.options.EscapeDot
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("RemoveRedundantBackticks")
internal class CopyFunctionNameTest {
    private fun testCopyFunctionName(
        options: CreamOptions,
        vararg triples: Triple<KSClassDeclaration, KSClassDeclaration, String>,
    ) {
        triples.forEach { (source, target, expectedFunName) ->
            val resultFunName = copyFunctionName(source, target, options)

            assertEquals(
                expectedFunName,
                resultFunName,
            )
        }
    }

    @Test
    fun `under-package, lower-camel-case`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`under-package`,
            escapeDot = EscapeDot.`lower-camel-case`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyToTarget",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyToStateSuccess",
        ),
    )

    @Test
    fun `diff-parent, lower-camel-case`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`diff`,
            escapeDot = EscapeDot.`lower-camel-case`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyToTargetTarget",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyToSuccess",
        ),
    )

    @Test
    fun `simple-name, lower-camel-case`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`simple-name`,
            escapeDot = EscapeDot.`lower-camel-case`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyToTarget",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyToSuccess",
        ),
    )

    @Test
    fun `full-name, lower-camel-case`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`full-name`,
            escapeDot = EscapeDot.`lower-camel-case`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyToMeTbstenExampleTargetTarget",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyToMeTbstenExampleStateSuccess",
        ),
    )

    @Test
    fun `under-package, replace-to-underscore`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`under-package`,
            escapeDot = EscapeDot.`replace-to-underscore`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyTo_Target",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyTo_State_Success",
        ),
    )

    @Test
    fun `diff-parent, replace-to-underscore`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`diff`,
            escapeDot = EscapeDot.`replace-to-underscore`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyTo_target_Target",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyTo_Success",
        ),
    )

    @Test
    fun `simple-name, replace-to-underscore`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`simple-name`,
            escapeDot = EscapeDot.`replace-to-underscore`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyTo_Target",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyTo_Success",
        ),
    )

    @Test
    fun `full-name, replace-to-underscore`() = testCopyFunctionName(
        CreamOptions(
            copyFunNamePrefix = "copyTo",
            copyFunNamingStrategy = CopyFunNamingStrategy.`full-name`,
            escapeDot = EscapeDot.`replace-to-underscore`,
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.source",
                underPackage = "Source",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example.target",
                underPackage = "Target",
            ),
            "copyTo_me_tbsten_example_target_Target",
        ),
        Triple(
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State",
            ),
            mockKSClassDeclaration(
                dummyPackage = "me.tbsten.example",
                underPackage = "State.Success",
            ),
            "copyTo_me_tbsten_example_State_Success",
        ),
    )

}

private fun mockKSClassDeclaration(
    dummyPackage: String,
    underPackage: String,
): KSClassDeclaration =
    mockk<KSClassDeclaration> {
        val dummyQualifiedName = buildString {
            append(dummyPackage)
            if (dummyPackage.isNotEmpty()) append(".")
            append(underPackage)
        }
        every { qualifiedName } returns mockk<KSName> {
            every { asString() } returns dummyQualifiedName
        }
        every { packageName } returns mockk<KSName> {
            every { asString() } returns dummyPackage
        }
        every { simpleName } returns mockk<KSName> {
            every { asString() } returns dummyQualifiedName.split(".").last()
        }
    }
