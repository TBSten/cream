package me.tbsten.cream.i18n

import kotlin.test.Test
import kotlin.test.assertEquals

class AppLanguageTest {
    @Test
    fun `ja を渡すと Japanese になる`() {
        assertEquals(AppLanguage.Japanese, AppLanguage.fromLanguageCode("ja"))
    }

    @Test
    fun `en を渡すと English になる`() {
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageCode("en"))
    }

    @Test
    fun `地域付きの言語タグでも言語部分で解決する`() {
        assertEquals(AppLanguage.Japanese, AppLanguage.fromLanguageCode("ja-JP"))
    }

    @Test
    fun `大文字小文字を区別せずに解決する`() {
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageCode("EN"))
    }

    @Test
    fun `未対応の言語コードは English にフォールバックする`() {
        assertEquals(AppLanguage.English, AppLanguage.fromLanguageCode("fr"))
    }
}
