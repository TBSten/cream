package me.tbsten.cream.ksp.feature.copyToChildren.scenario

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import me.tbsten.cream.CopyToChildren
import me.tbsten.cream.ksp.testing.generator.Generator
import me.tbsten.cream.ksp.testing.poet.Prop
import me.tbsten.cream.ksp.testing.poet.SnapshotScenario
import me.tbsten.cream.ksp.testing.poet.classNameOf
import me.tbsten.cream.ksp.testing.poet.snapshotScenarios
import me.tbsten.cream.ksp.testing.poet.valueClass

/**
 * A child whose primary-constructor parameter [paramName]: [paramType] is a PLAIN parameter (not a
 * property), while the parent's abstract property of the same name is satisfied by an `override`
 * with [overrideInitializer]. This is the only legal shape for a value-class conversion inside a
 * sealed hierarchy: the ctor param and the inherited property share a name but differ in type, so
 * the param must not itself be a property (which would be a conflicting override).
 */
private fun childWithRawParam(
    name: String,
    parent: ClassName,
    paramName: String,
    paramType: TypeName,
    overrideType: TypeName,
    overrideInitializer: String,
): TypeSpec =
    TypeSpec
        .classBuilder(name)
        .addSuperinterface(parent)
        .primaryConstructor(FunSpec.constructorBuilder().addParameter(paramName, paramType).build())
        .addProperty(
            PropertySpec
                .builder(paramName, overrideType)
                .addModifiers(KModifier.OVERRIDE)
                .initializer(overrideInitializer)
                .build(),
        ).build()

/**
 * Automatic value class mapping (issue #21, always on) through the `@CopyToChildren`
 * fan-out: the sealed parent is the copy source, so a child ctor param whose type differs from the
 * parent's same-named property only by a `value class` wrapper gets a wrap / unwrap default.
 */
internal fun valueClassMappingScenarios(): Generator<SnapshotScenario> =
    Generator.snapshotScenarios(
        // Parent `id: String` -> child ctor param `id: DomainId`: id gets `= DomainId(this.id)`.
        "wrapIntoValueClass" to
            copyToChildren(
                sealedInterfaceParent(
                    "State",
                    abstractProps = listOf(Prop("id")),
                    children =
                        listOf(
                            childWithRawParam(
                                "Loaded",
                                classNameOf("State"),
                                paramName = "id",
                                paramType = classNameOf("DomainId"),
                                overrideType = STRING,
                                overrideInitializer = "id.value",
                            ),
                        ),
                ),
                valueClass("DomainId"),
            ),
        // Parent `id: DomainId` -> child ctor param `id: String`: id gets `= this.id.value`.
        "unwrapFromValueClass" to
            copyToChildren(
                sealedInterfaceParent(
                    "State",
                    abstractProps = listOf(Prop("id", classNameOf("DomainId"))),
                    children =
                        listOf(
                            childWithRawParam(
                                "Loaded",
                                classNameOf("State"),
                                paramName = "id",
                                paramType = STRING,
                                overrideType = classNameOf("DomainId"),
                                overrideInitializer = "DomainId(id)",
                            ),
                        ),
                ),
                valueClass("DomainId"),
            ),
        // Parent `id: Int` vs DomainId(value: String): underlying type mismatch, id stays required.
        "underlyingTypeMismatch" to
            copyToChildren(
                sealedInterfaceParent(
                    "State",
                    abstractProps = listOf(Prop("id", INT)),
                    children =
                        listOf(
                            childWithRawParam(
                                "Loaded",
                                classNameOf("State"),
                                paramName = "id",
                                paramType = classNameOf("DomainId"),
                                overrideType = INT,
                                overrideInitializer = "id.value.length",
                            ),
                        ),
                ),
                valueClass("DomainId"),
            ),
        // @CopyToChildren.Exclude on the parent property wins over the conversion: the child ctor
        // param stays required, no default, and NO "@Exclude ... has no effect" warning
        // (suppressing a conversion default IS effective).
        "excludeSuppressesConversion" to
            copyToChildren(
                sealedInterfaceParent(
                    "State",
                    abstractProps =
                        listOf(
                            Prop("id", paramAnnotation = AnnotationSpec.builder(CopyToChildren.Exclude::class).build()),
                        ),
                    children =
                        listOf(
                            childWithRawParam(
                                "Loaded",
                                classNameOf("State"),
                                paramName = "id",
                                paramType = classNameOf("DomainId"),
                                overrideType = STRING,
                                overrideInitializer = "id.value",
                            ),
                        ),
                ),
                valueClass("DomainId"),
            ),
    )
