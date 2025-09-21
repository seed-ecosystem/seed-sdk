package seed.jsonrpc

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

internal sealed interface JsonRpcParams {
    val json: JsonElement

    fun serializable(): JsonRpcParamsSerializable =
        JsonRpcParamsSerializable(json)

    @JvmInline
    value class Object(override val json: JsonObject) : JsonRpcParams

    @JvmInline
    value class Array(override val json: JsonArray) : JsonRpcParams
}
