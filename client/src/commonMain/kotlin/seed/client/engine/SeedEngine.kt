package seed.client.engine

import kotlinx.io.IOException
import kotlinx.serialization.Serializable
import seed.jsonrpc.JsonRpcEngine

public class SeedEngine internal constructor(
    private val jsonrpc: JsonRpcEngine,
) {
    @Serializable
    private data class PushParams(
        val queueId: SeedEngineQueueIdSerializable,
        val message: SeedEngineMessageSerializable,
    )

    /**
     * Push [message] to the given [queue].
     */
    public suspend fun push(
        queueId: SeedEngineQueueId,
        message: SeedEngineMessage,
    ) {
        jsonrpc.executeOrThrow(
            method = "push",
            params =
            PushParams(
                queueId = queueId.serializable(),
                message = message.serializable(),
            ),
        )
    }

    /**
     * Returns the last record in the queue skipping
     * everything in-between. One might need that to
     * fetch the latest state of something (like user-profile).
     *
     * Returns null if the queue is empty.
     */
    public suspend fun last(queue: SeedEngineQueueId?): SeedEngineMessage? {
        TODO()
    }

    /**
     * Immediately returns an empty set if there
     * is no messages after [lastKnownNonce].
     * Usually you want to show loader when history is loading.
     *
     * Returns a chunk of messages and server decides what length
     * this chunk should be of.
     *
     * That being said, to get the full history one must call this
     * method until it returns an empty set.
     */
    public suspend fun history(
        queue: SeedEngineQueueId,
        lastKnownNonce: SeedEngineMessageNonce,
    ): Set<SeedEngineMessage> {
        TODO()
    }

    /**
     * Waits until the first set of messages arrive.
     * Will never return an empty set.
     */
    public suspend fun pull(
        queue: SeedEngineQueueId,
        lastKnownNonce: SeedEngineMessageNonce?,
    ): Set<SeedEngineMessage> {
        TODO()
    }

    public sealed interface Result<out T> {
        public fun getOrThrow(): T

        public data class IOError(val cause: IOException) : Result<Nothing> {
            override fun getOrThrow(): Nothing = throw cause
        }

        public data class Success<out T>(val value: T) : Result<T> {
            override fun getOrThrow(): T = value
        }
    }

    public companion object {
        public suspend fun <T> open(
            url: String,
            block: suspend (SeedEngine) -> T,
        ): Result<T> {
            val result =
                JsonRpcEngine.open(url) { jsonrpc ->
                    val engine = SeedEngine(jsonrpc)
                    block(engine)
                }
            return when (result) {
                is JsonRpcEngine.Result.Success ->
                    Result.Success(result.value)
                is JsonRpcEngine.Result.IOError ->
                    Result.IOError(result.cause)
            }
        }
    }
}
