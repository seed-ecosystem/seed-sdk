package seed.jsonrpc

import kotlinx.serialization.Serializable

@Serializable
internal data class JsonRpcNotificationSerializable(
    val method: JsonRpcMethodSerializable,
    val params: JsonRpcParamsSerializable,
) : JsonRpcEnvelopeSerializable {
    override fun typed(): JsonRpcNotification = JsonRpcNotification(
        method = method.typed(),
        params = params.typed(),
    )
}
