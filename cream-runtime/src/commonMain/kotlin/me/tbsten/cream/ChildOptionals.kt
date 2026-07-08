package me.tbsten.cream

/**
 * Expose the properties of a sealed type's children on the sealed type itself as **nullable
 * extension properties** — the blanket counterpart of [ParentOptional]. Annotate the sealed
 * parent, and every eligible property of every **transitive concrete leaf** gets an accessor
 * that returns the value when the receiver is that leaf, or `null` otherwise.
 *
 * # Example
 *
 * ```kt
 * @ChildOptionals
 * sealed interface MyState {
 *   data class Success(val data: String) : MyState
 *   data class Failure(val error: Throwable) : MyState
 *   data object Loading : MyState
 * }
 *
 * // Auto generate
 *
 * public val MyState.data: String?
 *   get() = when (this) {
 *     is MyState.Success -> data
 *     else -> null
 *   }
 *
 * public val MyState.error: Throwable?
 *   get() = when (this) {
 *     is MyState.Failure -> error
 *     else -> null
 *   }
 * ```
 *
 * # Which properties are picked up
 *
 * For each transitive concrete leaf (recursing through intermediate sealed types, like
 * [CopyToChildren]), the properties **declared by that leaf itself** (constructor + body) are
 * eligible, with these exceptions:
 *
 * - Properties already visible on the annotated sealed type (overrides included) are skipped —
 *   the member always wins over an extension, so an accessor would be dead code.
 * - `private` (and otherwise inaccessible) properties are skipped silently — a generated
 *   top-level accessor could not reference them.
 * - Extension properties are skipped silently — the accessor cannot supply their extension
 *   receiver.
 * - Properties whose type references a type parameter the annotated parent does not pin
 *   (e.g. `Tagged<M> : Parent` with `val meta: M`) are skipped **with a warning** — their type
 *   cannot be expressed on the parent receiver.
 *
 * `@Deprecated` on a swept property (or its leaf class) is propagated onto the generated
 * accessor, the same way as with [ParentOptional].
 *
 * Properties of several leaves that share one generated name are merged into a single accessor
 * with one `is` branch per leaf. All merged properties must have the same type (a type
 * mismatch is a compilation error).
 *
 * # Interplay with [ParentOptional]
 *
 * A [ParentOptional]-annotated property under a `@ChildOptionals` parent is generated **by
 * this annotation** (so the two never emit conflicting duplicate accessors), and its
 * [ParentOptional.propertyName] / [ParentOptional.kdoc] / [ParentOptional.visibility] are
 * honoured for that property's accessor.
 *
 * # Exclude
 *
 * Annotate a **child-class property** with [ChildOptionals.Exclude] to opt it out of the blanket
 * sweep: no accessor is generated from that contributor. `@ChildOptionals` has no per-property
 * opt-in the way [ParentOptional] does, so `@ChildOptionals.Exclude` is how you carve a single
 * property out of an otherwise blanket application. See [ChildOptionals.Exclude] for the precise
 * semantics (including how it interacts with merging and with an explicit [ParentOptional]) and an
 * example. [ParentOptional] needs no exclude concept — it is opt-in, so simply do not annotate a
 * property you do not want lifted.
 *
 * @property kdoc Custom KDoc (description / examples) injected into every generated accessor's
 *   KDoc, the same way as cream's other source annotations. See [KDoc].
 * @property visibility Visibility modifier of the generated extension properties. Defaults to
 *   [CopyVisibility.INHERIT]: the `cream.defaultVisibility` option applies first, then each
 *   accessor inherits the narrowest visibility among the sealed parent, the child, and the
 *   property. Forcing [CopyVisibility.PUBLIC] is a compilation error when an accessor's
 *   signature would expose an internal symbol (the sealed parent or a property type).
 *
 * @see ParentOptional
 * @see CopyToChildren
 * @see ChildOptionals.Exclude
 * @see CopyVisibility
 * @see KDoc
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public annotation class ChildOptionals(
    val kdoc: KDoc = KDoc(),
    val visibility: CopyVisibility = CopyVisibility.INHERIT,
) {
    /**
     * Opt a **child-class property** out of the [ChildOptionals] sweep: cream generates **no
     * accessor** from that contributor, as if the property had never been declared for the purpose
     * of the sweep.
     *
     * # How this `.Exclude` differs from copy's `.Exclude`
     *
     * cream's mental model for every `.Exclude` is the same — *"opt this property out of the
     * annotation's automatic behaviour"* — but the automatic behaviour differs per annotation, so
     * the observable effect differs too:
     *
     * - For the copy annotations ([CopyTo.Exclude] / [CopyFrom.Exclude] / [CopyToChildren.Exclude] /
     *   the combine ones) the automatic behaviour is the `= this.<property>` **auto-copy default**,
     *   so excluding a property keeps its parameter but makes it **required** at the call site.
     * - For `@ChildOptionals` the automatic behaviour is the **auto-generated nullable accessor**,
     *   so excluding a property means **no accessor is generated** for it at all (there is nothing
     *   to make "required" — an accessor either exists or it does not).
     *
     * # Merge interaction
     *
     * When properties of several children resolve to the **same** generated accessor name (see the
     * merging section on [ChildOptionals]), an excluded contributor simply drops out of the merge —
     * its `is <Child> ->` branch is omitted while the other children still contribute. If **every**
     * contributor to a given name is excluded, no accessor is generated for that name at all.
     *
     * # Interaction with [ParentOptional]
     *
     * `@ChildOptionals.Exclude` only affects **sweep-discovered** properties. A property that is
     * **explicitly** annotated with [ParentOptional] is opted in by hand, and that opt-in wins: its
     * accessor is still generated (honouring [ParentOptional.propertyName] etc.) even if it also
     * carries `@ChildOptionals.Exclude`. This mirrors the ownership rule that a `@ParentOptional`
     * property under a `@ChildOptionals` parent is generated by `@ChildOptionals`'s pass — the
     * explicit opt-in beats the sweep opt-out.
     *
     * # No effect
     *
     * Applying `@ChildOptionals.Exclude` to a property the sweep would never have picked up anyway
     * — one whose enclosing class is not part of a `@ChildOptionals` hierarchy, or one already
     * skipped for another reason (private, an extension property, already visible on the parent, or
     * carrying an unpinned type parameter) — has no effect and emits a KSP warning, the same way as
     * cream's other unmatched `@Exclude` warnings.
     *
     * # Example
     *
     * ```kt
     * @ChildOptionals
     * sealed interface MyState {
     *   data class Success(
     *     val data: String,
     *     @ChildOptionals.Exclude val trace: String,  // opted out — no accessor generated
     *   ) : MyState
     *   data object Loading : MyState
     * }
     *
     * // Generated: only `data` gets an accessor; `trace` is skipped.
     *
     * public val MyState.data: String?
     *   get() = when (this) {
     *     is MyState.Success -> data
     *     else -> null
     *   }
     * // (no `MyState.trace` accessor)
     * ```
     *
     * @see ChildOptionals
     * @see ParentOptional
     * @see CopyToChildren.Exclude
     */
    @Target(AnnotationTarget.PROPERTY)
    public annotation class Exclude
}
