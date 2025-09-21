package seed.client.engine

import kotlin.io.encoding.Base64.Default as Base64

@JvmInline
public value class SeedEngineMessageData(public val bytes: ByteArray) {
    public fun serializable(): SeedEngineMessageDataSerializable {
        val string = Base64.encode(bytes)
        return SeedEngineMessageDataSerializable(string)
    }
}
