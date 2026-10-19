// port-lint: source de.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.BooleanDeserialize
import io.github.kotlinmania.serdecore.de.CharDeserialize
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.F32Deserialize
import io.github.kotlinmania.serdecore.de.F64Deserialize
import io.github.kotlinmania.serdecore.de.I16Deserialize
import io.github.kotlinmania.serdecore.de.I32Deserialize
import io.github.kotlinmania.serdecore.de.I64Deserialize
import io.github.kotlinmania.serdecore.de.I8Deserialize
import io.github.kotlinmania.serdecore.de.StringDeserialize
import io.github.kotlinmania.serdecore.de.U16Deserialize
import io.github.kotlinmania.serdecore.de.U32Deserialize
import io.github.kotlinmania.serdecore.de.U64Deserialize
import io.github.kotlinmania.serdecore.de.U8Deserialize
import io.github.kotlinmania.serdecore.de.UnitDeserialize
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.de.nullableDeserialize
import io.github.kotlinmania.serdecore.de.value.intoDeserializer
import io.github.kotlinmania.serdecore.de.Deserializer as SerdeDeserializer

/**
 * A simple deserializer that works with plain strings.
 */
public class Deserializer(
    public val input: String,
) : SerdeDeserializer {
    public companion object {
        /**
         * Construct a new deserializer for the given string.
         */
        public fun new(input: String): Deserializer = Deserializer(input)
    }

    override fun <V> deserializeAny(visitor: Visitor<V>): SerdeResult<V> = deserializeStr(visitor)

    override fun <V> deserializeBool(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toBooleanStrictOrNull()
                ?: return SerdeResult.failure(Error.Parse("boolean", "provided string was not `true` or `false`").toSerdeError())
        return visitor.visitBool(v)
    }

    override fun <V> deserializeI8(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toByteOrNull()
                ?: return SerdeResult.failure(Error.Parse("i8", "number out of range").toSerdeError())
        return visitor.visitI8(v)
    }

    override fun <V> deserializeI16(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toShortOrNull()
                ?: return SerdeResult.failure(Error.Parse("i16", "number out of range").toSerdeError())
        return visitor.visitI16(v)
    }

    override fun <V> deserializeI32(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toIntOrNull()
                ?: return SerdeResult.failure(Error.Parse("i32", "number out of range").toSerdeError())
        return visitor.visitI32(v)
    }

    override fun <V> deserializeI64(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toLongOrNull()
                ?: return SerdeResult.failure(Error.Parse("i64", "number out of range").toSerdeError())
        return visitor.visitI64(v)
    }

    override fun <V> deserializeU8(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toUByteOrNull()
                ?: return SerdeResult.failure(Error.Parse("u8", "number out of range").toSerdeError())
        return visitor.visitU8(v)
    }

    override fun <V> deserializeU16(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toUShortOrNull()
                ?: return SerdeResult.failure(Error.Parse("u16", "number out of range").toSerdeError())
        return visitor.visitU16(v)
    }

    override fun <V> deserializeU32(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toUIntOrNull()
                ?: return SerdeResult.failure(Error.Parse("u32", "number out of range").toSerdeError())
        return visitor.visitU32(v)
    }

    override fun <V> deserializeU64(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toULongOrNull()
                ?: return SerdeResult.failure(Error.Parse("u64", "number out of range").toSerdeError())
        return visitor.visitU64(v)
    }

    override fun <V> deserializeF32(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toFloatOrNull()
                ?: return SerdeResult.failure(Error.Parse("f32", "invalid float").toSerdeError())
        return visitor.visitF32(v)
    }

    override fun <V> deserializeF64(visitor: Visitor<V>): SerdeResult<V> {
        val v =
            input.toDoubleOrNull()
                ?: return SerdeResult.failure(Error.Parse("f64", "invalid float").toSerdeError())
        return visitor.visitF64(v)
    }

    override fun <V> deserializeChar(visitor: Visitor<V>): SerdeResult<V> {
        if (input.length != 1) {
            return SerdeResult.failure(Error.Parse("char", "expected a single character").toSerdeError())
        }
        return visitor.visitChar(input[0])
    }

    override fun <V> deserializeStr(visitor: Visitor<V>): SerdeResult<V> = visitor.visitBorrowedStr(input)

    override fun <V> deserializeString(visitor: Visitor<V>): SerdeResult<V> = deserializeStr(visitor)

    override fun <V> deserializeBytes(visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("bytes").toSerdeError())

    override fun <V> deserializeByteBuf(visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("bytes").toSerdeError())

    override fun <V> deserializeOption(visitor: Visitor<V>): SerdeResult<V> =
        if (input.isEmpty()) {
            visitor.visitNone()
        } else {
            visitor.visitSome(this)
        }

    override fun <V> deserializeUnit(visitor: Visitor<V>): SerdeResult<V> =
        if (input.isEmpty()) {
            visitor.visitUnit()
        } else {
            SerdeResult.failure(Error.Message("expected empty string for unit").toSerdeError())
        }

    override fun <V> deserializeUnitStruct(name: String, visitor: Visitor<V>): SerdeResult<V> = deserializeUnit(visitor)

    override fun <V> deserializeNewtypeStruct(name: String, visitor: Visitor<V>): SerdeResult<V> =
        visitor.visitNewtypeStruct(this)

    override fun <V> deserializeSeq(visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("seq").toSerdeError())

    override fun <V> deserializeTuple(len: Int, visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("tuple").toSerdeError())

    override fun <V> deserializeTupleStruct(name: String, len: Int, visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("tuple struct").toSerdeError())

    override fun <V> deserializeMap(visitor: Visitor<V>): SerdeResult<V> =
        SerdeResult.failure(Error.ImpossibleDeserialization("map").toSerdeError())

    override fun <V> deserializeStruct(
        name: String,
        fields: List<String>,
        visitor: Visitor<V>,
    ): SerdeResult<V> = SerdeResult.failure(Error.ImpossibleDeserialization("struct").toSerdeError())

    override fun <V> deserializeEnum(
        name: String,
        variants: List<String>,
        visitor: Visitor<V>,
    ): SerdeResult<V> = visitor.visitEnum(input.intoDeserializer())

    override fun <V> deserializeIdentifier(visitor: Visitor<V>): SerdeResult<V> = deserializeStr(visitor)

    override fun <V> deserializeIgnoredAny(visitor: Visitor<V>): SerdeResult<V> = deserializeAny(visitor)
}

/**
 * Deserialize an instance of type T from a string of plain text using the given [Deserialize].
 *
 * This deserializes the string into an object with the [Deserializer]
 * and returns it. This requires that the type is a simple one
 * (integer, string etc.).
 */
public fun <T> fromStr(s: String, deserialize: Deserialize<T>): SerdeResult<T> = deserialize.deserialize(Deserializer.new(s))

/**
 * Deserialize a String from a string of plain text.
 */
public fun fromStr(s: String): SerdeResult<String> = fromStr(s, StringDeserialize)

/**
 * Deserialize a Boolean from a string of plain text.
 */
public fun fromStrBool(s: String): SerdeResult<Boolean> = fromStr(s, BooleanDeserialize)

/**
 * Deserialize a Byte from a string of plain text.
 */
public fun fromStrByte(s: String): SerdeResult<Byte> = fromStr(s, I8Deserialize)

/**
 * Deserialize a Short from a string of plain text.
 */
public fun fromStrShort(s: String): SerdeResult<Short> = fromStr(s, I16Deserialize)

/**
 * Deserialize an Int from a string of plain text.
 */
public fun fromStrInt(s: String): SerdeResult<Int> = fromStr(s, I32Deserialize)

/**
 * Deserialize a Long from a string of plain text.
 */
public fun fromStrLong(s: String): SerdeResult<Long> = fromStr(s, I64Deserialize)

/**
 * Deserialize a UByte from a string of plain text.
 */
public fun fromStrUByte(s: String): SerdeResult<UByte> = fromStr(s, U8Deserialize)

/**
 * Deserialize a UShort from a string of plain text.
 */
public fun fromStrUShort(s: String): SerdeResult<UShort> = fromStr(s, U16Deserialize)

/**
 * Deserialize a UInt from a string of plain text.
 */
public fun fromStrUInt(s: String): SerdeResult<UInt> = fromStr(s, U32Deserialize)

/**
 * Deserialize a ULong from a string of plain text.
 */
public fun fromStrULong(s: String): SerdeResult<ULong> = fromStr(s, U64Deserialize)

/**
 * Deserialize a Float from a string of plain text.
 */
public fun fromStrFloat(s: String): SerdeResult<Float> = fromStr(s, F32Deserialize)

/**
 * Deserialize a Double from a string of plain text.
 */
public fun fromStrDouble(s: String): SerdeResult<Double> = fromStr(s, F64Deserialize)

/**
 * Deserialize a Char from a string of plain text.
 */
public fun fromStrChar(s: String): SerdeResult<Char> = fromStr(s, CharDeserialize)

/**
 * Deserialize a Unit from a string of plain text.
 */
public fun fromStrUnit(s: String): SerdeResult<Unit> = fromStr(s, UnitDeserialize)

/**
 * Deserialize a nullable type from a string of plain text.
 */
public fun <T> fromStrNullable(s: String, deserialize: Deserialize<T>): SerdeResult<T?> =
    fromStr(s, nullableDeserialize(deserialize))
