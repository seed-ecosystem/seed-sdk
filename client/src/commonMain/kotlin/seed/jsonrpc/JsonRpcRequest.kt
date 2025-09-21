package seed.jsonrpc

internal data class JsonRpcRequest(
    val id: JsonRpcId,
    val method: JsonRpcMethod,
    val params: JsonRpcParams,
) : JsonRpcEnvelope {
    override fun serializable(): JsonRpcRequestSerializable =
        JsonRpcRequestSerializable(
            id = id.serializable(),
            method = method.serializable(),
            params = params.serializable(),
        )
}
