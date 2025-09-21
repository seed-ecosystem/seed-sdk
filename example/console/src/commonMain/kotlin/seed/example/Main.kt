package seed.example

import seed.engine.SeedEngine
import seed.engine.SeedEngineMessage
import seed.engine.SeedEngineMessageData
import seed.engine.SeedEngineMessageNonce
import seed.engine.SeedEngineMessageSignature
import seed.engine.SeedEngineQueueId
import kotlin.random.Random

private val url = "ws://localhost:8080/ws"

suspend fun main() {
    val result = SeedEngine.open(url) { engine ->
        val queueIdBytes = Random.nextBytes(SeedEngineQueueId.SIZE)
        val queueId = SeedEngineQueueId.orThrow(queueIdBytes)

        val nonce = SeedEngineMessageNonce(0)
        val data = SeedEngineMessageData(queueIdBytes)
        val signature = SeedEngineMessageSignature(queueIdBytes)
        val message = SeedEngineMessage(nonce, data, signature)

        engine.push(queueId, message)

        println("Hello World!")
    }
    println(result)
}
