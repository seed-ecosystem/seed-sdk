package seed.jsonrpc

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

internal class JsonRpcIncomingEngine(
    val requests: ReceiveChannel<JsonRpcRequest>,
    val notifications: ReceiveChannel<JsonRpcNotification>,
    private val responses: SendChannel<Response>,
) {
    suspend fun await(id: JsonRpcId): JsonRpcResponse {
        val deferred = CompletableDeferred<JsonRpcResponse>()
        responses.send(Response.Register(id, deferred))
        return try {
            deferred.await()
        } catch (exception: CancellationException) {
            responses.send(Response.Remove(id))
            throw exception
        }
    }

    sealed interface Response {
        data class Register(
            val id: JsonRpcId,
            val deferred: CompletableDeferred<JsonRpcResponse>,
        ) : Response

        data class Remove(val id: JsonRpcId) : Response

        data class Handle(val rpc: JsonRpcResponse) : Response
    }

    companion object {
        fun launchIn(
            scope: CoroutineScope,
            transport: JsonRpcTransportEngine,
        ): JsonRpcIncomingEngine {
            val requests = Channel<JsonRpcRequest>()
            val notifications = Channel<JsonRpcNotification>()
            val responses = Channel<Response>()

            scope.launch {
                handleIncoming(transport, requests, notifications, responses)
            }

            scope.launch {
                handleResponses(responses)
            }

            return JsonRpcIncomingEngine(requests, notifications, responses)
        }

        private suspend fun handleIncoming(
            transport: JsonRpcTransportEngine,
            requests: SendChannel<JsonRpcRequest>,
            notifications: SendChannel<JsonRpcNotification>,
            responses: SendChannel<Response>,
        ) {
            for (envelope in transport.incoming) {
                when (envelope) {
                    is JsonRpcRequest ->
                        requests.send(envelope)
                    is JsonRpcNotification ->
                        notifications.send(envelope)
                    is JsonRpcResponse ->
                        responses.send(Response.Handle(envelope))
                }
            }
        }

        private suspend fun handleResponses(
            responses: ReceiveChannel<Response>,
        ) {
            val map = mutableMapOf<JsonRpcId, Response.Register>()

            try {
                handleResponsesImpl(responses, map)
            } catch (throwable: Throwable) {
                cleanupMap(map, throwable)
                throw throwable
            }
        }

        private suspend fun handleResponsesImpl(
            responses: ReceiveChannel<Response>,
            map: MutableMap<JsonRpcId, Response.Register>,
        ) {
            for (response in responses) {
                when (response) {
                    is Response.Register -> {
                        map[response.id] = response
                    }
                    is Response.Remove -> {
                        map.remove(response.id)
                    }
                    is Response.Handle -> {
                        val register = map.remove(response.rpc.id)
                        if (register != null) {
                            register.deferred.complete(response.rpc)
                        }
                    }
                }
            }
        }

        private fun cleanupMap(
            map: Map<JsonRpcId, Response.Register>,
            throwable: Throwable,
        ) {
            for ((_, deferred) in map.values) {
                deferred.completeExceptionally(throwable)
            }
        }
    }
}
