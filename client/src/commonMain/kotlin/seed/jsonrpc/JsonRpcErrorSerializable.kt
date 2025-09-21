package seed.jsonrpc

import kotlinx.serialization.Serializable

@Serializable
internal class JsonRpcErrorSerializable(val code: Long, val message: String) {
    fun typed(): JsonRpcError = JsonRpcError(code, message)
}
