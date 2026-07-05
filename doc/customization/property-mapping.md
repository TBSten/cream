[← README](../../README.md) | [日本語](./property-mapping.ja.md)

# Property mapping (.Map)

You can use `@CopyTo.Map`, `@CopyFrom.Map`, `@CombineTo.Map`, and `@CombineFrom.Map` to map
properties between source and target classes. This is useful when the property names differ
between the source and target but you want to copy values between them.

> [!NOTE]
> `.Map` is not [`@CopyMapping`](../copy.md#copymapping), which generates a copy function *between two classes*
> you cannot modify.
>
> The `.Map` annotations on this page instead bind a single parameter of a generated
> function to a differently-named property.

## CopyTo.Map / CopyFrom.Map

```kt
import me.tbsten.cream.CopyTo

@CopyTo(DataModel::class)
data class DomainModel(
    @CopyTo.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// usage
val domainModel = DomainModel(domainId = "id-1")
val dataModel: DataModel = domainModel.copyToDataModel()
// dataModel.dataId == "id-1"
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun DomainModel.copyToDataModel(
    dataId: String = this.domainId, // domainId is mapped to dataId
): DataModel = ...
```

</details>

```kt
@CopyFrom(DataModel::class)
data class DomainModel(
    @CopyFrom.Map("dataId")
    val domainId: String,
)

data class DataModel(
    val dataId: String,
)

// usage
val dataModel = DataModel(dataId = "id-1")
val domainModel: DomainModel = dataModel.copyToDomainModel()
// domainModel.domainId == "id-1"
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun DataModel.copyToDomainModel(
    domainId: String = this.dataId, // dataId is mapped to domainId
): DomainModel = ...
```

</details>

## CombineTo.Map / CombineFrom.Map

Property mapping works the same way when copying from multiple source classes to a single target class.

**Specifying the mapping on the source side:**

```kt
@CombineTo(TargetState::class)
data class SourceA(
    @CombineTo.Map("targetProperty")
    val sourceProperty: String,
)

@CombineTo(TargetState::class)
data class SourceB(
    val otherProperty: Int,
)

data class TargetState(
    val targetProperty: String,
    val otherProperty: Int,
)

// usage
val sourceA = SourceA(sourceProperty = "value")
val sourceB = SourceB(otherProperty = 42)
val target: TargetState = sourceA.copyToTargetState(sourceB)
// target.targetProperty == "value", target.otherProperty == 42
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty is mapped to targetProperty
    otherProperty: Int = sourceB.otherProperty,
): TargetState = ...
```

</details>

**Specifying the mapping on the target side:**

```kt
data class SourceA(
    val sourceProperty: String,
)

data class SourceB(
    val otherSourceProperty: Int,
)

@CombineFrom(SourceA::class, SourceB::class)
data class TargetState(
    @CombineFrom.Map("sourceProperty")
    val targetProperty: String,
    @CombineFrom.Map("otherSourceProperty")
    val otherProperty: Int,
)

// usage
val sourceA = SourceA(sourceProperty = "value")
val sourceB = SourceB(otherSourceProperty = 42)
val target: TargetState = sourceA.copyToTargetState(sourceB)
// target.targetProperty == "value", target.otherProperty == 42
```

<details>
<summary>Generated code</summary>

```kt
// auto generate
fun SourceA.copyToTargetState(
    sourceB: SourceB,
    targetProperty: String = this.sourceProperty, // sourceProperty is mapped to targetProperty
    otherProperty: Int = sourceB.otherSourceProperty, // otherSourceProperty is mapped to otherProperty
): TargetState = ...
```

</details>

## SealedCopy.Map

`@SealedCopy.Map` carries the same "map to a differently-named counterpart" meaning, but it is
placed on a parameter of the `@SealedCopy.Via` delegate function, binding that parameter to a
differently-named abstract property of the sealed parent.

See [Sealed copy — @SealedCopy](../sealed-copy.md) for details and examples.

## See also

- [Excluding auto-copy defaults (.Exclude)](./exclude.md) — remove a parameter's auto-copy default instead of mapping it
- [Copy — @CopyTo / @CopyFrom / @CopyMapping](../copy.md)
- [Combine — @CombineTo / @CombineFrom / @CombineMapping](../combine.md)
- [Sealed copy — @SealedCopy](../sealed-copy.md)
