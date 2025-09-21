package seed.jsonrpc

import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

internal class JsonRpcTransportEngine(
    val incoming: ReceiveChannel<JsonRpcEnvelope>,
    val outgoing: SendChannel<JsonRpcEnvelope>,
    val json: Json,
) {
    companion object {
        fun launchIn(
            scope: CoroutineScope,
            session: DefaultWebSocketSession,
            json: Json,
        ): JsonRpcTransportEngine {
            val incoming = Channel<JsonRpcEnvelope>()
            val outgoing = Channel<JsonRpcEnvelope>()

            scope.launch {
                session.incoming.forEachTextFrame { frame ->
                    val text = frame.readText()
                    println("<< $text")
                    val envelope = json.decodeJsonRpcEnvelope(text)
                    incoming.send(envelope)
                }
            }

            scope.launch {
                for (envelope in outgoing) {
                    val text = json.encodeJsonRpcEnvelope(envelope)
                    println(">> $text")
                    val frame = Frame.Text(text)
                    session.send(frame)
                }
            }

            return JsonRpcTransportEngine(incoming, outgoing, json)
        }

        private suspend inline fun ReceiveChannel<Frame>.forEachTextFrame(
            block: (Frame.Text) -> Unit,
        ) {
            for (frame in this) {
                if (frame !is Frame.Text) continue
                block(frame)
            }
        }

        private fun Json.decodeJsonRpcEnvelope(
            string: String,
        ): JsonRpcEnvelope {
            val jsonObject = parseToJsonObject(string)
            return if ("method" in jsonObject) {
                if ("id" in jsonObject) {
                    decodeJsonRpcRequest(jsonObject)
                } else {
                    decodeJsonRpcNotification(jsonObject)
                }
            } else {
                decodeJsonRpcResponse(jsonObject)
            }
        }

        private fun Json.decodeJsonRpcRequest(
            element: JsonElement,
        ): JsonRpcRequest =
            decodeFromJsonElement<JsonRpcRequestSerializable>(element)
                .typed()

        private fun Json.decodeJsonRpcNotification(
            element: JsonElement,
        ): JsonRpcNotification =
            decodeFromJsonElement<JsonRpcNotificationSerializable>(element)
                .typed()

        private fun Json.decodeJsonRpcResponse(
            element: JsonElement,
        ): JsonRpcResponse =
            decodeFromJsonElement<JsonRpcResponseSerializable>(element)
                .typed()

        private fun Json.parseToJsonObject(string: String): JsonObject =
            parseToJsonElement(string).jsonObject

        private fun Json.encodeJsonRpcEnvelope(
            envelope: JsonRpcEnvelope,
        ): String = when (envelope) {
            is JsonRpcRequest ->
                encodeToString(envelope.serializable())
            is JsonRpcResponse ->
                encodeToString(envelope.serializable())
            is JsonRpcNotification ->
                encodeToString(envelope.serializable())
        }
    }
}
