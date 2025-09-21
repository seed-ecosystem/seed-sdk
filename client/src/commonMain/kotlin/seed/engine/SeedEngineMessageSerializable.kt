package seed.engine

import kotlinx.serialization.Serializable

@Serializable
public data class SeedEngineMessageSerializable(
    val nonce: SeedEngineMessageNonceSerializable,
    val data: SeedEngineMessageDataSerializable,
    val signature: SeedEngineMessageSignatureSerializable,
) {
    public fun typed(): SeedEngineMessage = SeedEngineMessage(
        nonce = nonce.typed(),
        data = data.typed(),
        signature = signature.typed(),
    )
}
