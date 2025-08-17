package me.tbsten.cream.ksp.options

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.tbsten.cream.ksp.InvalidCreamOptionException

internal class CreamOptionsTest {

    @Test
    fun `default values are correct`() {
        val options = mapOf<String, String>().toCreamOptions()
        
        assertEquals("copyTo", options.copyFunNamePrefix)
        assertEquals("mutableCopyTo", options.mutableCopyFunNamePrefix)
        assertEquals(CopyFunNamingStrategy.`under-package`, options.copyFunNamingStrategy)
        assertEquals(EscapeDot.`lower-camel-case`, options.escapeDot)
        assertEquals(false, options.notCopyToObject)
    }

    @Test
    fun `custom values are applied correctly`() {
        val options = mapOf(
            "cream.copyFunNamePrefix" to "customPrefix",
            "cream.mutableCopyFunNamePrefix" to "customMutablePrefix",
            "cream.copyFunNamingStrategy" to "simple-name",
            "cream.escapeDot" to "replace-to-underscore",
            "cream.notCopyToObject" to "true"
        ).toCreamOptions()
        
        assertEquals("customPrefix", options.copyFunNamePrefix)
        assertEquals("customMutablePrefix", options.mutableCopyFunNamePrefix)
        assertEquals(CopyFunNamingStrategy.`simple-name`, options.copyFunNamingStrategy)
        assertEquals(EscapeDot.`replace-to-underscore`, options.escapeDot)
        assertEquals(true, options.notCopyToObject)
    }

    @Test
    fun `throws error when copyFunNamePrefix equals mutableCopyFunNamePrefix`() {
        val options = mapOf(
            "cream.copyFunNamePrefix" to "samePrefix",
            "cream.mutableCopyFunNamePrefix" to "samePrefix"
        )
        
        val exception = assertFailsWith<InvalidCreamOptionException> {
            options.toCreamOptions()
        }
        
        assertEquals(
            "copyFunNamePrefix and mutableCopyFunNamePrefix cannot be the same: samePrefix",
            exception.message
        )
        assertEquals(
            "Set different values for copyFunNamePrefix and mutableCopyFunNamePrefix",
            exception.solution
        )
    }

    @Test
    fun `throws error when default values are the same`() {
        // This test should pass since the default values are different
        // copyTo != mutableCopyTo
        val options = mapOf<String, String>().toCreamOptions()
        assertEquals("copyTo", options.copyFunNamePrefix)
        assertEquals("mutableCopyTo", options.mutableCopyFunNamePrefix)
    }
}
