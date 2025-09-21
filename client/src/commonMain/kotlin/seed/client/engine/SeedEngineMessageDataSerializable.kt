package seed.client.engine

import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64.Default as Base64

@Serializable
@JvmInline
public value class SeedEngineMessageDataSerializable(
    public val string: String,
) {
    public fun typed(): SeedEngineMessageData {
        val bytes = Base64.decode(string)
        return SeedEngineMessageData(bytes)
    }
}
