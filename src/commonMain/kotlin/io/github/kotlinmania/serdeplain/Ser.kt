// port-lint: source ser.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.ser.SerializeMap
import io.github.kotlinmania.serdecore.ser.SerializeSeq
import io.github.kotlinmania.serdecore.ser.SerializeStruct
import io.github.kotlinmania.serdecore.ser.SerializeStructVariant
import io.github.kotlinmania.serdecore.ser.SerializeTuple
import io.github.kotlinmania.serdecore.ser.SerializeTupleStruct
import io.github.kotlinmania.serdecore.ser.SerializeTupleVariant
import io.github.kotlinmania.serdecore.ser.Serializer as SerdeSerializer

public typealias Ok = String
public typealias SerializeSeq = io.github.kotlinmania.serdecore.ser.SerializeSeq<String>
public typealias SerializeTuple = io.github.kotlinmania.serdecore.ser.SerializeTuple<String>
public typealias SerializeTupleStruct = io.github.kotlinmania.serdecore.ser.SerializeTupleStruct<String>
public typealias SerializeTupleVariant = io.github.kotlinmania.serdecore.ser.SerializeTupleVariant<String>
public typealias SerializeMap = io.github.kotlinmania.serdecore.ser.SerializeMap<String>
public typealias SerializeStruct = io.github.kotlinmania.serdecore.ser.SerializeStruct<String>
public typealias SerializeStructVariant = io.github.kotlinmania.serdecore.ser.SerializeStructVariant<String>

/**
 * A simple serializer that can dump out strings.
 */
public object Serializer : SerdeSerializer<String> {
    override fun serializeBool(v: Boolean): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeU8(v: UByte): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeU16(v: UShort): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeU32(v: UInt): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeU64(v: ULong): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeI8(v: Byte): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeI16(v: Short): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeI32(v: Int): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeI64(v: Long): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeF32(v: Float): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeF64(v: Double): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeChar(v: Char): SerdeResult<String> = SerdeResult.success(v.toString())

    override fun serializeStr(v: String): SerdeResult<String> = SerdeResult.success(v)

    override fun serializeBytes(v: ByteArray): SerdeResult<String> =
        SerdeResult.failure(Error.ImpossibleSerialization("bytes").toSerdeError())

    override fun serializeUnit(): SerdeResult<String> = SerdeResult.success("")

    override fun serializeUnitStruct(name: String): SerdeResult<String> =
        SerdeResult.failure(Error.ImpossibleSerialization("unit struct").toSerdeError())

    override fun serializeUnitVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
    ): SerdeResult<String> = SerdeResult.success(variant)

    override fun <T : Serialize> serializeNewtypeStruct(
        name: String,
        value: T,
    ): SerdeResult<String> = value.serialize(this)

    override fun <T : Serialize> serializeNewtypeVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        value: T,
    ): SerdeResult<String> =
        SerdeResult.failure(Error.ImpossibleSerialization("newtype variant").toSerdeError())

    override fun serializeNone(): SerdeResult<String> = SerdeResult.success("")

    override fun <T : Serialize> serializeSome(value: T): SerdeResult<String> = value.serialize(this)

    override fun serializeSeq(len: Int?): SerdeResult<SerializeSeq<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("seq").toSerdeError())

    override fun serializeTuple(len: Int): SerdeResult<SerializeTuple<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("tuple").toSerdeError())

    override fun serializeTupleStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeTupleStruct<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("tuple struct").toSerdeError())

    override fun serializeTupleVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeTupleVariant<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("tuple variant").toSerdeError())

    override fun serializeMap(len: Int?): SerdeResult<SerializeMap<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("map").toSerdeError())

    override fun serializeStruct(
        name: String,
        len: Int,
    ): SerdeResult<SerializeStruct<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("struct").toSerdeError())

    override fun serializeStructVariant(
        name: String,
        variantIndex: UInt,
        variant: String,
        len: Int,
    ): SerdeResult<SerializeStructVariant<String>> =
        SerdeResult.failure(Error.ImpossibleSerialization("struct variant").toSerdeError())
}

/**
 * Serialize the given data value as a plain string.
 *
 * This serializes an object with the Serializer into a string and then
 * returns it. This requires that the type is a simple one (integer, string,
 * etc.).
 */
public fun <T : Serialize> toString(value: T): SerdeResult<String> = value.serialize(Serializer)

/**
 * Serialize a Boolean value as a plain string.
 */
public fun toString(value: Boolean): SerdeResult<String> = Serializer.serializeBool(value)

/**
 * Serialize a Byte value as a plain string.
 */
public fun toString(value: Byte): SerdeResult<String> = Serializer.serializeI8(value)

/**
 * Serialize a Short value as a plain string.
 */
public fun toString(value: Short): SerdeResult<String> = Serializer.serializeI16(value)

/**
 * Serialize an Int value as a plain string.
 */
public fun toString(value: Int): SerdeResult<String> = Serializer.serializeI32(value)

/**
 * Serialize a Long value as a plain string.
 */
public fun toString(value: Long): SerdeResult<String> = Serializer.serializeI64(value)

/**
 * Serialize a UByte value as a plain string.
 */
public fun toString(value: UByte): SerdeResult<String> = Serializer.serializeU8(value)

/**
 * Serialize a UShort value as a plain string.
 */
public fun toStringUShort(value: UShort): SerdeResult<String> = Serializer.serializeU16(value)

/**
 * Serialize a UInt value as a plain string.
 */
public fun toString(value: UInt): SerdeResult<String> = Serializer.serializeU32(value)

/**
 * Serialize a ULong value as a plain string.
 */
public fun toString(value: ULong): SerdeResult<String> = Serializer.serializeU64(value)

/**
 * Serialize a Float value as a plain string.
 */
public fun toString(value: Float): SerdeResult<String> = Serializer.serializeF32(value)

/**
 * Serialize a Double value as a plain string.
 */
public fun toString(value: Double): SerdeResult<String> = Serializer.serializeF64(value)

/**
 * Serialize a Char value as a plain string.
 */
public fun toStringChar(value: Char): SerdeResult<String> = Serializer.serializeChar(value)

/**
 * Serialize a String value as a plain string.
 */
public fun toString(value: String): SerdeResult<String> = Serializer.serializeStr(value)

/**
 * Serialize a Unit value as a plain string.
 */
public fun toString(value: Unit): SerdeResult<String> = Serializer.serializeUnit()

/**
 * Serialize a Boolean value as a plain string.
 */
public fun toStringBool(value: Boolean): SerdeResult<String> = toString(value)

/**
 * Serialize a Byte value as a plain string.
 */
public fun toStringByte(value: Byte): SerdeResult<String> = toString(value)

/**
 * Serialize a Short value as a plain string.
 */
public fun toStringShort(value: Short): SerdeResult<String> = toString(value)

/**
 * Serialize an Int value as a plain string.
 */
public fun toStringInt(value: Int): SerdeResult<String> = toString(value)

/**
 * Serialize a Long value as a plain string.
 */
public fun toStringLong(value: Long): SerdeResult<String> = toString(value)

/**
 * Serialize a UByte value as a plain string.
 */
public fun toStringUByte(value: UByte): SerdeResult<String> = toString(value)

/**
 * Serialize a UInt value as a plain string.
 */
public fun toStringUInt(value: UInt): SerdeResult<String> = toString(value)

/**
 * Serialize a ULong value as a plain string.
 */
public fun toStringULong(value: ULong): SerdeResult<String> = toString(value)

/**
 * Serialize a Float value as a plain string.
 */
public fun toStringFloat(value: Float): SerdeResult<String> = toString(value)

/**
 * Serialize a Double value as a plain string.
 */
public fun toStringDouble(value: Double): SerdeResult<String> = toString(value)

/**
 * Serialize a Unit value as a plain string.
 */
public fun toStringUnit(value: Unit = Unit): SerdeResult<String> = toString(value)
