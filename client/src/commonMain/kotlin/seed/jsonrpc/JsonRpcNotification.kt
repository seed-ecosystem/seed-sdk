package seed.jsonrpc

internal data class JsonRpcNotification(
    val method: JsonRpcMethod,
    val params: JsonRpcParams,
) : JsonRpcEnvelope {
    override fun serializable(): JsonRpcNotificationSerializable =
        JsonRpcNotificationSerializable(
            method = method.serializable(),
            params = params.serializable(),
        )
}
