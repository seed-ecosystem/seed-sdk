package seed.client.engine

import kotlin.io.encoding.Base64.Default as Base64

// This Identifier is Public.
// 256-bytes
@JvmInline
public value class SeedEngineQueueId private constructor(
    public val bytes: ByteArray,
) {
    public fun serializable(): SeedEngineQueueIdSerializable {
        val string = Base64.encode(bytes)
        return SeedEngineQueueIdSerializable(string)
    }

    public companion object {
        public const val SIZE: Int = 256

        public fun orThrow(bytes: ByteArray): SeedEngineQueueId {
            require(bytes.size == SIZE)
            return SeedEngineQueueId(bytes)
        }
    }
}
