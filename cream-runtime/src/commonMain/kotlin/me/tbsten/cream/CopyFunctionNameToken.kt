@file:Suppress("ktlint:standard:property-naming")

package me.tbsten.cream

/*
 * Naming tokens you can embed in an annotation's `funName` to compose the name of the
 * copy/combine function cream generates.
 *
 * `funName` is a **template string**. cream replaces every token below with a concrete
 * piece of the generated name, so you can keep cream's derived name and only add a
 * prefix/suffix, or build a name from scratch:
 *
 * ```kt
 * import me.tbsten.cream.*
 *
 * @CopyTo(UiState.Success::class)                                       // copyToUiStateSuccess (default)
 * @CopyTo(UiState.Success::class, funName = DefaultCopyFunctionName + "OrNull") // copyToUiStateSuccessOrNull
 * @CopyTo(UiState.Success::class, funName = "to" + CopyTargetSimpleName)        // toSuccess
 * @CopyTo(UiState.Success::class, funName = "to_" + copy_target_under_package)  // to_uistate_success
 * @CopyTo(UiState.Success::class, funName = "toState")                          // toState (plain literal)
 * data class Source(/* ... */)
 * ```
 *
 * Because the tokens are `const val`, they can be concatenated with `+` in an annotation
 * argument and still be a compile-time constant.
 *
 * Each `CopyTarget*` concept comes in two casings:
 * - Pascal (`CopyTargetSimpleName`): each dotted segment is upper-cased and joined, e.g.
 *   `UiState.Success` -> `UiStateSuccess`. Designed to sit after a lower-case prefix.
 * - snake (`copy_target_simple_name`): each dotted segment is lower-cased and joined with
 *   `_`, e.g. `UiState.Success` -> `uistate_success`.
 *
 * The `CopyTarget*` tokens render the target name independently of the project-wide
 * `cream.copyFunNamingStrategy` / `cream.escapeDot` options, so the name you compose is
 * stable. Only [DefaultCopyFunctionName] follows those options (it *is* cream's derived
 * name). For an annotation that generates more than one function (multiple targets, a
 * sealed target, or a reversible mapping), include a `CopyTarget*` token so each
 * generated function gets a distinct name.
 */

/**
 * The full name cream would generate for the function by default — prefix
 * (`cream.copyFunNamePrefix`) + the target name rendered by `cream.copyFunNamingStrategy`
 * and `cream.escapeDot`. This is the default value of every `funName`, so omitting
 * `funName` keeps cream's existing behaviour. Embed it to keep the derived name and only
 * add a prefix/suffix, e.g. `funName = DefaultCopyFunctionName + "OrNull"`.
 */
public const val DefaultCopyFunctionName: String = "{{cream:DefaultCopyFunctionName}}"

/** Target simple name in Pascal case, e.g. `UiState.Success` -> `Success`. */
public const val CopyTargetSimpleName: String = "{{cream:CopyTargetSimpleName}}"

/** Target simple name in snake case, e.g. `UiState.Success` -> `success`. */
public const val copy_target_simple_name: String = "{{cream:copy_target_simple_name}}"

/** Target name below its package, Pascal case, e.g. `com.example.UiState.Success` -> `UiStateSuccess`. */
public const val CopyTargetUnderPackage: String = "{{cream:CopyTargetUnderPackage}}"

/** Target name below its package, snake case, e.g. `com.example.UiState.Success` -> `uistate_success`. */
public const val copy_target_under_package: String = "{{cream:copy_target_under_package}}"

/** Target name below its outermost declaration, Pascal case, e.g. `UiState.Success` -> `Success`. */
public const val CopyTargetInnerName: String = "{{cream:CopyTargetInnerName}}"

/** Target name below its outermost declaration, snake case, e.g. `UiState.Success` -> `success`. */
public const val copy_target_inner_name: String = "{{cream:copy_target_inner_name}}"

/** Fully-qualified target name, Pascal case, e.g. `com.example.UiState.Success` -> `ComExampleUiStateSuccess`. */
public const val CopyTargetFullName: String = "{{cream:CopyTargetFullName}}"

/** Fully-qualified target name, snake case, e.g. `com.example.UiState.Success` -> `com_example_uistate_success`. */
public const val copy_target_full_name: String = "{{cream:copy_target_full_name}}"
