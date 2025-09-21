package seed.jsonrpc

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonElement

@Serializable
internal class JsonRpcResponseSerializable(
    val id: JsonRpcIdSerializable,
    val result: JsonElement? = null,
    val error: JsonRpcErrorSerializable? = null,
) : JsonRpcEnvelopeSerializable {
    init {
        if (result == null && error == null) {
            throw SerializationException(
                "Response can't have both result and error absent",
            )
        }
        if (result != null && error != null) {
            throw SerializationException(
                "Response can't have both result and error present",
            )
        }
    }

    override fun typed(): JsonRpcResponse = when {
        result != null -> JsonRpcResponse.Success(id.typed(), result)
        error != null -> JsonRpcResponse.Failure(id.typed(), error.typed())
        else -> error("unreachable")
    }
}
