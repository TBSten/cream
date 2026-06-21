package me.tbsten.cream.ksp.testing.smoke

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import com.tschuchort.compiletesting.KotlinCompilation
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import me.tbsten.cream.CopyTo
import me.tbsten.cream.ksp.options.CreamOptions
import me.tbsten.cream.ksp.testing.compile.CreamCompilationResult
import me.tbsten.cream.ksp.testing.compile.generatedSourceText
import me.tbsten.cream.ksp.testing.compile.runCompileSnapshotTest
import com.squareup.kotlinpoet.FunSpec as PoetFunSpec

/**
 * Smoke test for [runCompileSnapshotTest]: proves the snapshot wrapper actually compiles a KotlinPoet
 * [FileSpec] input through cream's KSP processor, exposes the [CreamCompilationResult] to `assertions`,
 * and returns it. Complements [CreamCompilationSmokeTest], which covers the lower-level
 * `compileWithCream`. The golden it writes (`Input` / `KSP options` / `Output:*` facets) is generated
 * with `-Dcream.snapshot.update=true` like every other snapshot.
 */
internal class RunCompileSnapshotTestSmokeTest :
    FunSpec({
        test("compiles a @CopyTo FileSpec, exposes the result to assertions, and returns it") {
            var observed: CreamCompilationResult? = null

            val result =
                runCompileSnapshotTest(
                    input = copyToInput(),
                    options = CreamOptions.default,
                    assertions = { compiled ->
                        withClue(compiled.messages) {
                            compiled.exitCode shouldBe KotlinCompilation.ExitCode.OK
                        }
                        withClue("generated:\n${compiled.generatedSourceText()}") {
                            compiled.generatedSourceText() shouldContain "copyToTarget"
                        }
                        observed = compiled
                    },
                )

            observed shouldBe result
            result.exitCode shouldBe KotlinCompilation.ExitCode.OK
        }
    })

private fun copyToInput(): FileSpec {
    val packageName = "smoke.compile"
    val target = ClassName(packageName, "Target")
    return FileSpec
        .builder(packageName, "Input")
        .addType(
            TypeSpec
                .classBuilder("Source")
                .addModifiers(KModifier.DATA)
                .addAnnotation(AnnotationSpec.builder(CopyTo::class).addMember("%T::class", target).build())
                .primaryConstructor(PoetFunSpec.constructorBuilder().addParameter("shared", STRING).build())
                .addProperty(PropertySpec.builder("shared", STRING).initializer("shared").build())
                .build(),
        ).addType(
            TypeSpec
                .classBuilder("Target")
                .addModifiers(KModifier.DATA)
                .primaryConstructor(
                    PoetFunSpec
                        .constructorBuilder()
                        .addParameter("shared", STRING)
                        .addParameter("extra", INT)
                        .build(),
                ).addProperty(PropertySpec.builder("shared", STRING).initializer("shared").build())
                .addProperty(PropertySpec.builder("extra", INT).initializer("extra").build())
                .build(),
        ).build()
}
