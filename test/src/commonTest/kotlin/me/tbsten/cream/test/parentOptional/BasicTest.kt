package me.tbsten.cream.test.parentOptional

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class BasicTest :
    FreeSpec({
        "Success のとき data アクセサから値が取れる" {
            val state: MyState = MyState.Success(data = "payload", message = "ok")
            state.data shouldBe "payload"
        }

        "Loading のとき data アクセサは null を返す" {
            val state: MyState = MyState.Loading
            state.data shouldBe null
        }

        "複数の子の同名プロパティは 1 つのアクセサにマージされ、どの子からも値が取れる" {
            val success: MyState = MyState.Success(data = "d", message = "from success")
            val failure: MyState = MyState.Failure(message = "from failure", code = 500)
            success.message shouldBe "from success"
            failure.message shouldBe "from failure"
        }

        "propertyName 指定でアクセサ名を変更できる" {
            val failure: MyState = MyState.Failure(message = "m", code = 404)
            val loading: MyState = MyState.Loading
            failure.errorCode shouldBe 404
            loading.errorCode shouldBe null
        }

        "中間 sealed 型と最上位 sealed 型の両方でアクセサが使える" {
            val rect = Shape.Polygon.Rect(corners = 4)
            val asShape: Shape = rect
            val asPolygon: Shape.Polygon = rect
            val circle: Shape = Shape.Circle
            asShape.corners shouldBe 4
            asPolygon.corners shouldBe 4
            circle.corners shouldBe null
        }

        "subtype 関係の子同士のマージでは、より派生した子のプロパティが返る" {
            val sportsCar: Vehicle = Vehicle.SportsCar(carLabel = "car", sportsLabel = "sports")
            val sedan: Vehicle = Vehicle.Sedan(carLabel = "sedan")
            // is Car の分岐が先に評価されると SportsCar でも carLabel ("car") が返ってしまう
            sportsCar.label shouldBe "sports"
            sedan.label shouldBe "sedan"
        }

        "中間 sealed 型 (Car) 受けのアクセサでも派生した子の分岐が有効" {
            val sportsCar: Vehicle.Car = Vehicle.SportsCar(carLabel = "car", sportsLabel = "sports")
            val sedan: Vehicle.Car = Vehicle.Sedan(carLabel = "sedan")
            sportsCar.label shouldBe "sports"
            // Car 自身のプロパティ (carLabel) は Car 受けアクセサの対象外 (member が使える) のため null
            sedan.label shouldBe null
        }

        "generic な親 (Box<T>) のアクセサは型パラメータを保ったまま値が取れる" {
            val filled: Box<String> = Box.Filled(item = "payload")
            val empty: Box<String> = Box.Empty()
            val item: String? = filled.item
            item shouldBe "payload"
            empty.item shouldBe null
        }

        "nullable プロパティのアクセサは String? のままで、両方の null が区別なく返る" {
            val present: Payload = Payload.Present(data = "value")
            val presentNull: Payload = Payload.Present(data = null)
            val missing: Payload = Payload.Missing
            present.data shouldBe "value"
            // プロパティ自体の null と「その子ではない」の null は区別されない (KDoc に注記)
            presentNull.data shouldBe null
            missing.data shouldBe null
        }

        "ハードキーワード名のプロパティもエスケープされたアクセサで参照できる" {
            val holder: Words = Words.KeywordHolder(`object` = "value")
            val none: Words = Words.None
            holder.`object` shouldBe "value"
            none.`object` shouldBe null
        }
    })
