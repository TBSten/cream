import dsl.alias
import dsl.ktlint
import dsl.libs
import dsl.plugin
import dsl.version
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class LintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                alias(libs.plugin("ktlintGradle"))
            }

            ktlint {
                version.set(libs.version("ktlint"))

                this
                filter {
                    exclude("**/generated/ksp/**")
                }
            }
        }
    }
}
