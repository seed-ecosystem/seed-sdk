package seed.jsonrpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
@JvmInline
internal value class JsonRpcParamsSerializable(val jsonElement: JsonElement) {
    init {
        ensureValid()
    }

    private fun ensureValid() {
        if (jsonElement is JsonObject) return
        if (jsonElement is JsonArray) return
        throw SerializationException("Params must be either Object or Array")
    }

    fun typed(): JsonRpcParams {
        if (jsonElement is JsonObject) {
            return JsonRpcParams.Object(jsonElement)
        }
        if (jsonElement is JsonArray) {
            return JsonRpcParams.Array(jsonElement)
        }
        error("unreachable")
    }
}
