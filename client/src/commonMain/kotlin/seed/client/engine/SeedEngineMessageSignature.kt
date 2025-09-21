package seed.client.engine

import kotlin.io.encoding.Base64.Default as Base64

// Proofs that you can write to channel. That signature is checked by server
// and clients. Server can't create a new signature, but can verify one.
// Signature is created for concatenated bytes of queueId + nonce.
@JvmInline
public value class SeedEngineMessageSignature(public val bytes: ByteArray) {
    public fun serializable(): SeedEngineMessageSignatureSerializable {
        val string = Base64.encode(bytes)
        return SeedEngineMessageSignatureSerializable(string)
    }
}
