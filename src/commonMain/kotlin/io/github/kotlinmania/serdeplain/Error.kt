// port-lint: source error.rs
package io.github.kotlinmania.serdeplain

import io.github.kotlinmania.serde.SerdeError
import io.github.kotlinmania.serdecore.StdError

/**
 * Errors created from this crate.
 */
public sealed class Error : StdError {
    /**
     * An impossible or unsupported operation was attempted.
     */
    public data class ImpossibleSerialization(
        public val type: String,
    ) : Error()

    /**
     * A certain deserialization is impossible.
     */
    public data class ImpossibleDeserialization(
        public val type: String,
    ) : Error()

    /**
     * Raised when parsing errors happen during deserialization.
     */
    public data class Parse(
        public val type: String,
        public val message: String,
    ) : Error()

    /**
     * An arbitrary error message.
     */
    public data class Message(
        public val message: String,
    ) : Error()

    /**
     * Format the error message into the given formatter buffer.
     */
    public fun fmt(f: StringBuilder) {
        f.append(toString())
    }

    /**
     * Format the error message into a string.
     */
    public fun fmt(): String = toString()

    /**
     * Create an error from a custom message.
     */
    public fun custom(msg: String): Error = Message(msg)

    override fun toString(): String =
        when (this) {
            is ImpossibleSerialization -> "cannot serialize non primitive type $type"
            is ImpossibleDeserialization -> "cannot deserialize to non primitive type $type"
            is Parse -> "cannot parse $type: $message"
            is Message -> message
        }

    /**
     * Converts this error into a SerdeError.
     */
    public fun toSerdeError(): SerdeError = SerdeError(toString())

    public companion object {
        /**
         * Create an error from a custom message.
         */
        public fun custom(msg: String): Error = Message(msg)
    }
}
