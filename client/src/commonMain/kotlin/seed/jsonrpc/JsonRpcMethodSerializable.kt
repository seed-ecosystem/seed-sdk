package seed.jsonrpc

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
internal value class JsonRpcMethodSerializable(val string: String) {
    fun typed(): JsonRpcMethod = JsonRpcMethod(string)
}
