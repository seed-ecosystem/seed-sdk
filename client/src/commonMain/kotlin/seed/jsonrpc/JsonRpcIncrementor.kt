package seed.jsonrpc

internal expect fun createJsonRpcIncrementor(): JsonRpcIncrementor

internal interface JsonRpcIncrementor {
    fun incrementAndGet(): Long
}
