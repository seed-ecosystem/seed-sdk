package seed.jsonrpc

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.takeFrom
import io.ktor.websocket.DefaultWebSocketSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart.UNDISPATCHED
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.job
import kotlinx.coroutines.plus
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

private val NULL = Any()

private val client =
    HttpClient(CIO) {
        install(WebSockets)
    }

internal class JsonRpcEngine(
    private val transport: JsonRpcTransportEngine,
    private val incomingEngine: JsonRpcIncomingEngine,
    val json: Json,
) {
    val requests: ReceiveChannel<JsonRpcRequest>
        get() = incomingEngine.requests

    val notifications: ReceiveChannel<JsonRpcNotification>
        get() = incomingEngine.notifications

    private val incrementor = createJsonRpcIncrementor()

    suspend inline fun <reified T> executeOrThrow(
        method: String,
        params: T,
        id: JsonRpcId = nextId(),
    ): JsonRpcResponse {
        val paramsJson = json.encodeToJsonElement(params)
        val params = JsonRpcParamsSerializable(paramsJson).typed()
        val request = JsonRpcRequest(id, JsonRpcMethod(method), params)
        return execute(request)
    }

    fun nextId(): JsonRpcId = JsonRpcId.Long(incrementor.incrementAndGet())

    suspend fun execute(request: JsonRpcRequest): JsonRpcResponse =
        coroutineScope {
            val deferred =
                async(start = UNDISPATCHED) {
                    incomingEngine.await(request.id)
                }
            transport.outgoing.send(request)
            deferred.await()
        }

    sealed interface Result<out T> {
        fun getOrThrow(): T

        data class IOError(val cause: IOException) : Result<Nothing> {
            override fun getOrThrow(): Nothing = throw cause
        }

        data class Success<out T>(val value: T) : Result<T> {
            override fun getOrThrow(): T = value
        }
    }

    companion object {
        suspend fun <T> open(
            url: String,
            json: Json = Json {
                ignoreUnknownKeys = true
            },
            block: suspend (JsonRpcEngine) -> T,
        ): Result<T> = try {
            engine(url, json) { engine ->
                val value = block(engine)
                Result.Success(value)
            }
        } catch (exception: IOException) {
            Result.IOError(exception)
        }

        private suspend inline fun <T> engine(
            url: String,
            json: Json,
            crossinline block: suspend (JsonRpcEngine) -> T,
        ): T {
            var value: Any? = NULL
            client.webSocket(url) { session, foreground, background ->
                val transport =
                    JsonRpcTransportEngine.launchIn(background, session, json)

                val incoming =
                    JsonRpcIncomingEngine.launchIn(background, transport)

                val engine = JsonRpcEngine(transport, incoming, json)

                value = block(engine)
            }
            @Suppress("UNCHECKED_CAST")
            return value as T
        }

        private suspend inline fun HttpClient.webSocket(
            url: String,
            crossinline block: suspend (
                session: DefaultWebSocketSession,
                foreground: CoroutineScope,
                background: CoroutineScope,
            ) -> Unit,
        ) {
            webSocket({ this.url.takeFrom(url) }) {
                val session = this
                coroutineScope {
                    val foreground = this
                    backgroundScope(foreground) { background ->
                        block(session, foreground, background)
                    }
                }
            }
        }

        private inline fun backgroundScope(
            parent: CoroutineScope,
            block: (CoroutineScope) -> Unit,
        ) {
            val background = parent + Job(parent.coroutineContext.job)
            try {
                block(background)
            } finally {
                background.cancel()
            }
        }
    }
}
