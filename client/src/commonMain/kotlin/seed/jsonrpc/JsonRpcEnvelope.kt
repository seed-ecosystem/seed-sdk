package seed.jsonrpc

internal sealed interface JsonRpcEnvelope {
    fun serializable(): JsonRpcEnvelopeSerializable
}
