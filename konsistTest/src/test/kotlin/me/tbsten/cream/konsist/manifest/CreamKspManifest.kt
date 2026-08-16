package me.tbsten.cream.konsist.manifest

import me.tbsten.cream.konsist.dsl.Manifest
import me.tbsten.cream.konsist.dsl.ManifestDirBuilder
import me.tbsten.cream.konsist.dsl.SourceSetManifestBuilder
import me.tbsten.cream.konsist.dsl.manifest

/**
 * :cream-ksp module の main sourceSet 内に配置することのできるファイル・ディレクトリに構造。
 * ここに設定されていないファイルは配置してはいけない。
 */
internal val creamKspManifest: Manifest =
    manifest {
        module(":cream-ksp").apply {
            sourceSet("main")
                .kspKotlinDir {
                    util()

                    options()

                    core()

                    feature()

                    kspEntryPoint()
                }
        }
    }

private fun SourceSetManifestBuilder.kspKotlinDir(block: ManifestDirBuilder.() -> Unit) = dir("kotlin/${ManifestConstants.KSP_BASE_PACKAGE_DIR}", block)
