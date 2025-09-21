package seed.jsonrpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull

@Serializable
@JvmInline
internal value class JsonRpcIdSerializable(val jsonPrimitive: JsonPrimitive) {
    init {
        ensureValid()
    }

    private fun ensureValid() {
        if (jsonPrimitive.isString) {
            return
        }
        if (jsonPrimitive.longOrNull != null) {
            return
        }
        error("JsonRpcId was neither long or string")
    }

    fun typed(): JsonRpcId {
        val long = jsonPrimitive.longOrNull
        if (long != null) {
            return JsonRpcId.Long(long)
        }
        if (jsonPrimitive.isString) {
            return JsonRpcId.String(jsonPrimitive.content)
        }
        error("unreachable")
    }
}
