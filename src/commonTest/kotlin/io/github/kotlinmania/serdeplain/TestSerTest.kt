// port-lint: tests tests/test_ser.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlin.test.Test
import kotlin.test.assertEquals

enum class TestEnum : Serialize {
    FooBarBaz,
    BlahBlah,
    ;

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
        val name =
            when (this) {
                FooBarBaz -> "foo_bar_baz"
                BlahBlah -> "blah_blah"
            }
        return serializer.serializeUnitVariant("TestEnum", ordinal.toUInt(), name)
    }

    override fun toString(): String =
        io.github.kotlinmania.serdeplain
            .toString(this)
            .getOrThrow()
}

data class NewInt(
    val value: Int,
) : Serialize {
    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
        val inner =
            object : Serialize {
                override fun <O> serialize(serializer: Serializer<O>): SerdeResult<O> = serializer.serializeI32(value)
            }
        return serializer.serializeNewtypeStruct("NewInt", inner)
    }

    override fun toString(): String =
        io.github.kotlinmania.serdeplain
            .toString(this)
            .getOrThrow()
}

class TestSerTest {
    @Test
    fun testBasics() {
        assertEquals("42", toString(42).getOrThrow())
        assertEquals("blafasel", toString("blafasel").getOrThrow())
        assertEquals("foo_bar_baz", toString(TestEnum.FooBarBaz).getOrThrow())
        assertEquals("blah_blah", toString(TestEnum.BlahBlah).getOrThrow())
        assertEquals("42", toString(NewInt(42)).getOrThrow())
        assertEquals("true", toString(true).getOrThrow())
        assertEquals("", toString(Unit).getOrThrow())
    }

    @Test
    fun testToString() {
        assertEquals("foo_bar_baz", TestEnum.FooBarBaz.toString())
        assertEquals("42", NewInt(42).toString())
    }
}
