// port-lint: source macros.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serde.SerdeResult
import io.github.kotlinmania.serdecore.de.Deserialize
import io.github.kotlinmania.serdecore.de.Unexpected
import io.github.kotlinmania.serdecore.de.Visitor
import io.github.kotlinmania.serdecore.ser.Serialize
import io.github.kotlinmania.serdecore.de.Deserializer as SerdeDeserializer
import io.github.kotlinmania.serdecore.ser.Serializer as SerdeSerializer

/**
 * Creates a [Deserialize] implementation for a type that can be parsed from a string.
 *
 * This provides the functionality of the derive deserialize from string macro.
 */
public class FromStrDeserialize<T>(
    public val expectation: String,
    public val parse: (String) -> T?,
) : Deserialize<T> {
    override fun <D : SerdeDeserializer> deserialize(deserializer: D): SerdeResult<T> {
        val visitor =
            object : Visitor<T> {
                override fun expecting(): String = expectation

                override fun visitStr(v: String): SerdeResult<T> {
                    val parsed = parse(v)
                    return if (parsed != null) {
                        SerdeResult.success(parsed)
                    } else {
                        SerdeResult.failure(SerdeError.invalidValue(Unexpected.Str(v), this))
                    }
                }
            }
        return deserializer.deserializeStr(visitor)
    }
}

/**
 * Derives a [Deserialize] implementation for a type given an expectation and parse function.
 */
public fun <T> deriveDeserializeFromFromStr(
    expectation: String,
    parse: (String) -> T?,
): Deserialize<T> = FromStrDeserialize(expectation, parse)

/**
 * Implements [Serialize] for a type using its string representation.
 *
 * This provides the functionality of the derive serialize from display macro.
 */
public class DisplaySerialize(
    public val display: () -> String,
) : Serialize {
    override fun <Ok> serialize(serializer: SerdeSerializer<Ok>): SerdeResult<Ok> = serializer.serializeStr(display())
}

/**
 * Helper to serialize an object that provides a string representation.
 */
public fun deriveSerializeFromDisplay(display: () -> String): Serialize = DisplaySerialize(display)

/**
 * Formats a serializable value into its plain string representation.
 */
public fun <T : Serialize> deriveDisplayFromSerialize(value: T): String =
    toString(value).getOrThrow()

/**
 * Parses a string into a deserializable value.
 */
public fun <T> deriveFromStrFromDeserialize(s: String, deserialize: Deserialize<T>): SerdeResult<T> =
    fromStr(s, deserialize)
