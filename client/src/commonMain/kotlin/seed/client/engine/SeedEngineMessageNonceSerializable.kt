package seed.client.engine

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
public value class SeedEngineMessageNonceSerializable(public val long: Long) {
    public fun typed(): SeedEngineMessageNonce = SeedEngineMessageNonce(long)
}
