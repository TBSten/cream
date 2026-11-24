package me.tbsten.cream.test.architecture.test.architectureTest

import me.tbsten.katachi.dsl.DeclarationContainerScope

fun DeclarationContainerScope.architectureTestGroup() =
    "ArchitectureTest".group {
        documented = false
        summary = "katachi の定義と、それを実行するテスト"

        projectArchitectureRole()
        projectArchitectureTestRole()
        roleDefinitionRole()
        groupDefinitionRole()
        definitionConstantRole()
        definitionSupportRole()
    }
