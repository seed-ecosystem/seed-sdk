package seed.jsonrpc

import java.util.concurrent.atomic.AtomicLong

internal actual fun createJsonRpcIncrementor(): JsonRpcIncrementor {
    val long = AtomicLong()
    return object : JsonRpcIncrementor {
        override fun incrementAndGet(): Long = long.incrementAndGet()
    }
}
