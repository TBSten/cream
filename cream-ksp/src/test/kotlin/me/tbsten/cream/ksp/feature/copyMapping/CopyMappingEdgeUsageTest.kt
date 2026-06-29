package me.tbsten.cream.ksp.feature.copyMapping

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

internal class CopyMappingEdgeUsageTest :
    FreeSpec({
        // issue #145: @CopyMapping maps two classes you usually do not own (external libraries), so
        // the generated copy function must land in the *holder* package (where the mapping is
        // declared), not the source class's package — otherwise it is generated into a package the
        // user does not control and cannot discover.
        "generates the copy function into the holder package, not the source package (issue #145)" {
            val result =
                compileWithCream {
                    "lib/Models.kt" source
                        """
                        package com.example.lib

                        data class LibSource(val name: String, val value: Int)

                        data class LibTarget(val name: String, val value: Int)
                        """.trimIndent()
                    "mapping/Mapping.kt" source
                        """
                        package com.example.mapping

                        import com.example.lib.LibSource
                        import com.example.lib.LibTarget
                        import me.tbsten.cream.CopyMapping

                        @CopyMapping(LibSource::class, LibTarget::class)
                        object Mapping
                        """.trimIndent()
                }

            result.exitCode shouldBe KotlinCompilation.ExitCode.OK

            val generated = result.generatedSourceText()
            // The generated file declares the holder package and references the library types by
            // their fully-qualified names; it is never emitted into the source (com.example.lib) package.
            generated shouldContain "package com.example.mapping"
            generated shouldNotContain "package com.example.lib"
        }
    })
