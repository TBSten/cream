package me.tbsten.cream.ksp.feature.combineMapping

import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import me.tbsten.cream.ksp.testing.compile.compileWithCream
import me.tbsten.cream.ksp.testing.compile.generatedSourceText

internal class CombineMappingEdgeUsageTest :
    FreeSpec({
        // issue #145: like @CopyMapping, @CombineMapping combines classes you usually do not own, so
        // the generated combine function must land in the *holder* package (where the mapping is
        // declared), not the first source class's package.
        "generates the combine function into the holder package, not the source package (issue #145)" {
            val result =
                compileWithCream {
                    "lib/Models.kt" source
                        """
                        package com.example.lib

                        data class LibA(val nameA: String)

                        data class LibB(val valueB: Int)

                        data class Combined(val nameA: String, val valueB: Int)
                        """.trimIndent()
                    "mapping/Mapping.kt" source
                        """
                        package com.example.mapping

                        import com.example.lib.Combined
                        import com.example.lib.LibA
                        import com.example.lib.LibB
                        import me.tbsten.cream.CombineMapping

                        @CombineMapping(sources = [LibA::class, LibB::class], target = Combined::class)
                        object Mapping
                        """.trimIndent()
                }

            result.exitCode shouldBe KotlinCompilation.ExitCode.OK

            val generated = result.generatedSourceText()
            generated shouldContain "package com.example.mapping"
            generated shouldNotContain "package com.example.lib"
        }
    })
