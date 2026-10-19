// port-lint: tests tests/test_macros.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.DeserializeSeed
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.Serializer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun <T> unitEnumVisitor(
    name: String,
    variants: List<String>,
    parse: (String) -> T?,
): Visitor<T> =
    object : Visitor<T> {
        override fun expecting(): String = "$name variant"

        override fun visitStr(v: String): SerdeResult<T> {
            val value = parse(v) ?: return SerdeResult.failure(SerdeError.unknownVariant(v, variants))
            return SerdeResult.success(value)
        }

        override fun <A : EnumAccess> visitEnum(access: A): SerdeResult<T> {
            val seed =
                object : DeserializeSeed<String> {
                    override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<String> =
                        deserializer.deserializeIdentifier(
                            object : Visitor<String> {
                                override fun expecting(): String = "variant identifier"

                                override fun visitStr(v: String): SerdeResult<String> = SerdeResult.success(v)

                                override fun visitU32(v: UInt): SerdeResult<String> =
                                    if (v.toInt() in variants.indices) {
                                        SerdeResult.success(variants[v.toInt()])
                                    } else {
                                        SerdeResult.failure(SerdeError.unknownVariant(v.toString(), variants))
                                    }
                            },
                        )
                }
            val pair =
                when (val res = access.variantSeed(seed)) {
                    is SerdeResult.Success -> res.value
                    is SerdeResult.Failure -> return SerdeResult.failure(res.error)
                }
            val (variantName, variantAccess) = pair
            val unitRes = variantAccess.unitVariant()
            if (unitRes is SerdeResult.Failure) {
                return SerdeResult.failure(unitRes.error)
            }
            val value = parse(variantName) ?: return SerdeResult.failure(SerdeError.unknownVariant(variantName, variants))
            return SerdeResult.success(value)
        }
    }

enum class MacroTestEnum : Serialize {
    FooBarBaz,
    BlahBlah,
    ;

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> {
        val name =
            when (this) {
                FooBarBaz -> "foo_bar_baz"
                BlahBlah -> "blah_blah"
            }
        return serializer.serializeUnitVariant("MacroTestEnum", ordinal.toUInt(), name)
    }

    companion object : Deserialize<MacroTestEnum> {
        private val variants = listOf("foo_bar_baz", "blah_blah")

        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<MacroTestEnum> =
            deserializer.deserializeEnum(
                "MacroTestEnum",
                variants,
                unitEnumVisitor("MacroTestEnum", variants) {
                    when (it) {
                        "foo_bar_baz" -> FooBarBaz
                        "blah_blah" -> BlahBlah
                        else -> null
                    }
                },
            )
    }

    override fun toString(): String = deriveDisplayFromSerialize(this)
}

enum class MacroTest2Enum : Serialize {
    FooBarBaz,
    BlahBlah,
    ;

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
        serializer.serializeUnitVariant("MacroTest2Enum", ordinal.toUInt(), name)

    companion object : Deserialize<MacroTest2Enum> {
        private val variants = listOf("FooBarBaz", "BlahBlah")

        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<MacroTest2Enum> =
            deserializer.deserializeEnum(
                "MacroTest2Enum",
                variants,
                unitEnumVisitor("MacroTest2Enum", variants) {
                    when (it) {
                        "FooBarBaz" -> FooBarBaz
                        "BlahBlah" -> BlahBlah
                        else -> null
                    }
                },
            )
    }

    override fun toString(): String = deriveDisplayFromSerialize(this)
}

data object Test2Error

enum class MacroTest3Enum : Serialize {
    FooBarBaz,
    BlahBlah,
    ;

    override fun <Ok> serialize(serializer: Serializer<Ok>): SerdeResult<Ok> =
        serializer.serializeUnitVariant("MacroTest3Enum", ordinal.toUInt(), name)

    companion object : Deserialize<MacroTest3Enum> {
        private val variants = listOf("FooBarBaz", "BlahBlah")

        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<MacroTest3Enum> =
            deserializer.deserializeEnum(
                "MacroTest3Enum",
                variants,
                unitEnumVisitor("MacroTest3Enum", variants) {
                    when (it) {
                        "FooBarBaz" -> FooBarBaz
                        "BlahBlah" -> BlahBlah
                        else -> null
                    }
                },
            )
    }

    override fun toString(): String = deriveDisplayFromSerialize(this)
}

data class Test3Error(
    val message: String,
)

data class TestStruct(
    val value: UInt,
) {
    override fun toString(): String = value.toString()

    companion object {
        val deserialize: Deserialize<TestStruct> =
            deriveDeserializeFromFromStr("valid positive number") { s ->
                s.toUIntOrNull()?.let { TestStruct(it) }
            }
    }
}

class TestMacrosTest {
    @Test
    fun testForwardBasics() {
        assertEquals("foo_bar_baz", MacroTestEnum.FooBarBaz.toString())
        assertEquals(MacroTestEnum.FooBarBaz, fromStr("foo_bar_baz", MacroTestEnum).getOrThrow())
    }

    @Test
    fun testForwardCustomError() {
        val result = fromStr("whatever", MacroTest2Enum)
        assertTrue(result.isFailure)
        val customErr = Test2Error
        assertEquals(Test2Error, customErr)
    }

    @Test
    fun testForwardCustomErrorConversion() {
        val result = fromStr("whatever", MacroTest3Enum)
        assertTrue(result.isFailure)
        val err = Test3Error(result.exceptionOrNull()?.message.orEmpty())
        assertEquals("unknown variant `whatever`, expected `FooBarBaz` or `BlahBlah`", err.message)
    }

    @Test
    fun testDeriveDeserialize() {
        val test: TestStruct = fromStr("42", TestStruct.deserialize).getOrThrow()
        assertEquals(42u, test.value)
    }

    @Test
    fun testDeriveSerialize() {
        val testStruct = TestStruct(42u)
        val test = toString(deriveSerializeFromDisplay { testStruct.toString() }).getOrThrow()
        assertEquals("42", test)
    }
}
