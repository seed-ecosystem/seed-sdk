package seed.engine

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlin.io.encoding.Base64.Default as Base64

// This Identifier is Public.
// 256-bytes
@JvmInline
@Serializable
public value class SeedEngineQueueIdSerializable(public val string: String) {
    init {
        val bytes = Base64.decode(string)
        check(bytes.size == SeedEngineQueueId.SIZE) {
            throw SerializationException("queueId size must be 256 bytes")
        }
    }

    public fun typed(): SeedEngineQueueId {
        val bytes = Base64.decode(string)
        return SeedEngineQueueId.orThrow(bytes)
    }
}
