package seed.jsonrpc

@JvmInline
internal value class JsonRpcMethod(val string: String) {
    fun serializable(): JsonRpcMethodSerializable =
        JsonRpcMethodSerializable(string)
}
