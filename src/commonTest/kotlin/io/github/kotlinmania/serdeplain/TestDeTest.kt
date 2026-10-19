// port-lint: tests tests/test_de.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.DeserializeSeed
import io.github.kotlinmania.serdecore.de.Deserializer
import io.github.kotlinmania.serdecore.de.EnumAccess
import io.github.kotlinmania.serdecore.de.I32Deserialize
import io.github.kotlinmania.serdecore.de.IgnoredAny
import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdecore.de.UnitDeserialize
import io.github.kotlinmania.serdecore.de.Visitor
import kotlin.test.Test
import kotlin.test.assertEquals

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

enum class TestDeEnum {
    FooBarBaz,
    BlahBlah,
    ;

    companion object : Deserialize<TestDeEnum> {
        private val variants = listOf("foo_bar_baz", "blah_blah")

        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<TestDeEnum> =
            deserializer.deserializeEnum(
                "TestDeEnum",
                variants,
                unitEnumVisitor("TestDeEnum", variants) {
                    when (it) {
                        "foo_bar_baz" -> FooBarBaz
                        "blah_blah" -> BlahBlah
                        else -> null
                    }
                },
            )
    }
}

enum class FooType {
    FooA,
    FooB,
    ;

    companion object : Deserialize<FooType> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<FooType> =
            deserializer.deserializeStr(
                object : Visitor<FooType> {
                    override fun expecting(): String = "FooType variant"

                    override fun visitStr(v: String): SerdeResult<FooType> =
                        when (v) {
                            "foo_a" -> SerdeResult.success(FooA)
                            "foo_b" -> SerdeResult.success(FooB)
                            else -> SerdeResult.failure(SerdeError.unknownVariant(v, listOf("foo_a", "foo_b")))
                        }
                },
            )
    }
}

enum class BarType {
    BarA,
    BarB,
    ;

    companion object : Deserialize<BarType> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<BarType> =
            deserializer.deserializeStr(
                object : Visitor<BarType> {
                    override fun expecting(): String = "BarType variant"

                    override fun visitStr(v: String): SerdeResult<BarType> =
                        when (v) {
                            "bar_a" -> SerdeResult.success(BarA)
                            "bar_b" -> SerdeResult.success(BarB)
                            else -> SerdeResult.failure(SerdeError.unknownVariant(v, listOf("bar_a", "bar_b")))
                        }
                },
            )
    }
}

sealed class TestUntagged {
    data class Foo(
        val value: FooType,
    ) : TestUntagged()

    data class Bar(
        val value: BarType,
    ) : TestUntagged()

    data class Other(
        val value: String,
    ) : TestUntagged()

    companion object : Deserialize<TestUntagged> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<TestUntagged> {
            val visitor =
                object : Visitor<TestUntagged> {
                    override fun expecting(): String = "untagged enum"

                    override fun visitStr(v: String): SerdeResult<TestUntagged> =
                        when (v) {
                            "foo_a" -> SerdeResult.success(Foo(FooType.FooA))
                            "foo_b" -> SerdeResult.success(Foo(FooType.FooB))
                            "bar_a" -> SerdeResult.success(Bar(BarType.BarA))
                            "bar_b" -> SerdeResult.success(Bar(BarType.BarB))
                            else -> SerdeResult.success(Other(v))
                        }
                }
            return deserializer.deserializeAny(visitor)
        }
    }
}

private data object DeNewIntVisitor : Visitor<DeNewInt> {
    override fun expecting(): String = "a newtype int"

    override fun <D : Deserializer> visitNewtypeStruct(deserializer: D): SerdeResult<DeNewInt> =
        I32Deserialize.deserialize(deserializer).map { DeNewInt(it) }
}

data class DeNewInt(
    val value: Int,
) {
    companion object : Deserialize<DeNewInt> {
        override fun <D : Deserializer> deserialize(deserializer: D): SerdeResult<DeNewInt> =
            deserializer.deserializeNewtypeStruct("DeNewInt", DeNewIntVisitor)
    }
}

class TestDeTest {
    @Test
    fun testBasics() {
        assertEquals("aha", fromStr("aha", StringDeserialize).getOrThrow())
        assertEquals(42, fromStrInt("42").getOrThrow())
        assertEquals(true, fromStrBool("true").getOrThrow())
        assertEquals(false, fromStrBool("false").getOrThrow())
        assertEquals(Unit, fromStrUnit("").getOrThrow())
        assertEquals(null, fromStrNullable("", UnitDeserialize).getOrThrow())
        assertEquals("42", fromStrNullable("42", StringDeserialize).getOrThrow())
        assertEquals(TestDeEnum.BlahBlah, fromStr("blah_blah", TestDeEnum).getOrThrow())
        assertEquals(DeNewInt(42), fromStr("42", DeNewInt).getOrThrow())
        fromStr("blah_blah", IgnoredAny).getOrThrow()
    }

    @Test
    fun testUntaggedEnum() {
        val pairs =
            listOf(
                "foo_a" to TestUntagged.Foo(FooType.FooA),
                "foo_b" to TestUntagged.Foo(FooType.FooB),
                "bar_a" to TestUntagged.Bar(BarType.BarA),
                "bar_b" to TestUntagged.Bar(BarType.BarB),
                "other" to TestUntagged.Other("other"),
            )

        for ((string, expected) in pairs) {
            assertEquals(expected, fromStr(string, TestUntagged).getOrThrow())
        }
    }

    @Test
    fun testFromStr() {
        assertEquals(TestDeEnum.FooBarBaz, fromStr("foo_bar_baz", TestDeEnum).getOrThrow())
    }
}
