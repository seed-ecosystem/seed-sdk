package seed.engine

// Auto-Increments. Serves as a proof that you received
// all messages before.
@JvmInline
public value class SeedEngineMessageNonce(public val long: Long) {
    public fun serializable(): SeedEngineMessageNonceSerializable =
        SeedEngineMessageNonceSerializable(long)
}
