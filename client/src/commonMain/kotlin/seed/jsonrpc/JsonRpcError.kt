package seed.jsonrpc

internal data class JsonRpcError(val code: Long, val message: String) {
    fun serializable(): JsonRpcErrorSerializable =
        JsonRpcErrorSerializable(code, message)
}
