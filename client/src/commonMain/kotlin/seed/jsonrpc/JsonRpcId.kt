package seed.jsonrpc

import kotlinx.serialization.json.JsonPrimitive

internal sealed interface JsonRpcId {
    fun serializable(): JsonRpcIdSerializable = when (this) {
        is String -> JsonRpcIdSerializable(JsonPrimitive(string))
        is Long -> JsonRpcIdSerializable(JsonPrimitive(long))
    }

    @JvmInline
    value class String(val string: kotlin.String) : JsonRpcId

    @JvmInline
    value class Long(val long: kotlin.Long) : JsonRpcId
}
