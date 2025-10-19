package me.tbsten.cream.test.copyMapping

import kotlin.test.Test
import kotlin.test.assertEquals

class CopyMappingTest {
    @Test
    fun libXModelToLibYModel() {
        val libXModel =
            LibXModel(
                shareProp = "shared",
                xProp = 42,
            )

        val result =
            libXModel.copyToLibYModel(
                yProp = 100,
            )

        val expected =
            LibYModel(
                shareProp = "shared",
                yProp = 100,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libXModelToLibYModelWithOverride() {
        val libXModel =
            LibXModel(
                shareProp = "shared",
                xProp = 42,
            )

        val result =
            libXModel.copyToLibYModel(
                shareProp = "overridden",
                yProp = 100,
            )

        val expected =
            LibYModel(
                shareProp = "overridden",
                yProp = 100,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libYModelToLibZModel() {
        val libYModel =
            LibYModel(
                shareProp = "shared",
                yProp = 200,
            )

        val result =
            libYModel.copyToLibZModel(
                zProp = 300,
            )

        val expected =
            LibZModel(
                shareProp = "shared",
                zProp = 300,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libYModelToLibZModelWithOverride() {
        val libYModel =
            LibYModel(
                shareProp = "shared",
                yProp = 200,
            )

        val result =
            libYModel.copyToLibZModel(
                shareProp = "new-shared",
                zProp = 400,
            )

        val expected =
            LibZModel(
                shareProp = "new-shared",
                zProp = 400,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libXModelToLibYModelWithPropertyMapping() {
        // Test that xProp from LibXModel maps to yProp in LibYModel
        // via the CopyMapping.Map property mapping
        val libXModel =
            LibXModel(
                shareProp = "shared",
                xProp = 42,
            )

        val result = libXModel.copyToLibYModel()

        val expected =
            LibYModel(
                shareProp = "shared",
                yProp = 42,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libXModelToLibYModelWithPropertyMappingAndOverride() {
        // Test that property mapping can be overridden
        val libXModel =
            LibXModel(
                shareProp = "shared",
                xProp = 42,
            )

        val result =
            libXModel.copyToLibYModel(
                yProp = 999,
            )

        val expected =
            LibYModel(
                shareProp = "shared",
                yProp = 999,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libWModelToLibVModelWithCanReverse() {
        val libWModel =
            LibWModel(
                shareProp = "shared",
                wProp = 3.14,
            )

        val result =
            libWModel.copyToLibVModel(
                vProp = true,
            )

        val expected =
            LibVModel(
                shareProp = "shared",
                vProp = true,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libVModelToLibWModelWithCanReverse() {
        val libVModel =
            LibVModel(
                shareProp = "shared",
                vProp = false,
            )

        val result =
            libVModel.copyToLibWModel(
                wProp = 2.71,
            )

        val expected =
            LibWModel(
                shareProp = "shared",
                wProp = 2.71,
            )

        assertEquals(expected, result)
    }

    @Test
    fun libWModelToLibVModelWithCanReverseAndOverride() {
        val libWModel =
            LibWModel(
                shareProp = "shared",
                wProp = 3.14,
            )

        val result =
            libWModel.copyToLibVModel(
                shareProp = "overridden",
                vProp = true,
            )

        val expected =
            LibVModel(
                shareProp = "overridden",
                vProp = true,
            )

        assertEquals(expected, result)
    }
}
