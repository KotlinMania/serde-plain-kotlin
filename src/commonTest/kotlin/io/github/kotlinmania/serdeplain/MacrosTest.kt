// port-lint: tests macros.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlin.test.Test
import kotlin.test.assertEquals

class MacrosTest {
    data class MyType(
        val value: String,
    ) : Serialize {
        override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> = serializer.serializeStr(value)

        fun fmt(f: StringBuilder) {
            f.append(value)
        }

        fun fmt(): String = value

        override fun toString(): String = deriveDisplayFromSerialize(this)
    }

    object Inner {
        data class MyType(
            val value: String,
        ) : Serialize {
            override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> = serializer.serializeStr(value)

            fun fmt(f: StringBuilder) {
                f.append(value)
            }

            fun fmt(): String = value

            override fun toString(): String = deriveDisplayFromSerialize(this)
        }
    }

    @Test
    fun testDeriveDisplayFromSerializeLifetimes() {
        assertEquals("x", MyType("x").toString())
        assertEquals("x", Inner.MyType("x").toString())
    }

    @Test
    fun testDeriveSerializeFromDisplayLifetimes() {
        val first = deriveSerializeFromDisplay { MyType("x").fmt() }
        val second = deriveSerializeFromDisplay { Inner.MyType("x").fmt() }

        assertEquals("x", toString(first).getOrThrow())
        assertEquals("x", toString(second).getOrThrow())
    }
}
