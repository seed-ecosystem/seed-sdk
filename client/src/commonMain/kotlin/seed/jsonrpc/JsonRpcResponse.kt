package seed.jsonrpc

import kotlinx.serialization.json.JsonElement

internal sealed interface JsonRpcResponse : JsonRpcEnvelope {
    val id: JsonRpcId
    val result: JsonElement?
    val error: JsonRpcError?

    fun ensureSuccess(): Success = when (this) {
        is Success -> this
        is Failure -> error("$this")
    }

    override fun serializable(): JsonRpcResponseSerializable =
        JsonRpcResponseSerializable(
            id = id.serializable(),
            result = result,
            error = error?.serializable(),
        )

    data class Success(
        override val id: JsonRpcId,
        override val result: JsonElement,
    ) : JsonRpcResponse {
        override val error: Nothing? get() = null
    }

    data class Failure(
        override val id: JsonRpcId,
        override val error: JsonRpcError,
    ) : JsonRpcResponse {
        override val result: Nothing? get() = null
    }
}
