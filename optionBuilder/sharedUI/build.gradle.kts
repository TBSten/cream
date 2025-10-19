import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import org.jetbrains.compose.ExperimentalComposeLibrary

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.buildKonfig)
    id("buildLogic.lint")
}

kotlin {
    jvm()

    js { browser() }

    compilerOptions.optIn.addAll(
        "me.tbsten.cream.InternalCreamApi",
    )

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(project(":cream-runtime"))
            implementation(project(":cream-ksp:shared"))
            implementation(libs.kotlinxCoroutines)
            implementation(libs.kotlinxSerializationJson)
            implementation(libs.materialKolor)
            implementation(libs.kodeview)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

buildkonfig {
    packageName = "me.tbsten.cream"
    defaultConfigs {
        buildConfigField(STRING, "creamVersion", libs.versions.cream.get())
        buildConfigField(STRING, "kspVersion", libs.versions.ksp.get())
    }
}
