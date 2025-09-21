package seed.client.engine

public data class SeedEngineMessage(
    val nonce: SeedEngineMessageNonce,
    val data: SeedEngineMessageData,
    val signature: SeedEngineMessageSignature,
) {
    public fun serializable(): SeedEngineMessageSerializable =
        SeedEngineMessageSerializable(
            nonce = nonce.serializable(),
            data = data.serializable(),
            signature = signature.serializable(),
        )
}
