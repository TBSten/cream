package me.tbsten.cream.test.parentOptional

import me.tbsten.cream.ParentOptional

sealed interface MyState {
    data class Success(
        @ParentOptional val data: String,
        @ParentOptional val message: String,
    ) : MyState

    data class Failure(
        @ParentOptional val message: String,
        @ParentOptional(propertyName = "errorCode") val code: Int,
    ) : MyState

    data object Loading : MyState
}

// 中間 sealed 型 (Polygon) と最上位 (Shape) の両方にアクセサが生成される
sealed interface Shape {
    sealed interface Polygon : Shape {
        data class Rect(
            @ParentOptional val corners: Int,
        ) : Polygon
    }

    data object Circle : Shape
}

// subtype 関係にある子同士が同じアクセサにマージされる場合、
// より派生した子 (SportsCar) の分岐が先に評価される
sealed interface Vehicle {
    sealed class Car(
        @ParentOptional(propertyName = "label") val carLabel: String,
    ) : Vehicle

    class SportsCar(
        carLabel: String,
        @ParentOptional(propertyName = "label") val sportsLabel: String,
    ) : Car(carLabel)

    class Sedan(
        carLabel: String,
    ) : Car(carLabel)
}

// 親が直接 pin する型パラメータ (Filled<E> : Box<E>) を参照するプロパティ:
// `val <T> Box<T>.item: T?` が生成される
sealed interface Box<T> {
    data class Filled<E>(
        @ParentOptional val item: E,
    ) : Box<E>

    class Empty<E> : Box<E>
}

// nullable プロパティ: アクセサ型は String? のまま (`?` は重ねない)。
// null は「その子ではない」「プロパティ自体が null」の両方を意味し得る (生成 KDoc に注記)
sealed interface Payload {
    data class Present(
        @ParentOptional val data: String?,
    ) : Payload

    data object Missing : Payload
}

// Kotlin ハードキーワードのプロパティ名はバッククォートでエスケープされて生成される
sealed interface Words {
    data class KeywordHolder(
        @ParentOptional val `object`: String,
    ) : Words

    data object None : Words
}
