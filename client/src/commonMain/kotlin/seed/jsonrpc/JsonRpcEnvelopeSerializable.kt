package seed.jsonrpc

internal sealed interface JsonRpcEnvelopeSerializable {
    fun typed(): JsonRpcEnvelope
}
