package seed.jsonrpc

import kotlinx.serialization.Serializable

@Serializable
internal data class JsonRpcRequestSerializable(
    val id: JsonRpcIdSerializable,
    val method: JsonRpcMethodSerializable,
    val params: JsonRpcParamsSerializable,
) : JsonRpcEnvelopeSerializable {
    override fun typed(): JsonRpcRequest = JsonRpcRequest(
        id = id.typed(),
        method = method.typed(),
        params = params.typed(),
    )
}
