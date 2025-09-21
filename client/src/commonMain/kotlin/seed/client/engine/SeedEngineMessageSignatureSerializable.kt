package seed.client.engine

import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64.Default as Base64

@Serializable
@JvmInline
public value class SeedEngineMessageSignatureSerializable(
    public val string: String,
) {
    public fun typed(): SeedEngineMessageSignature {
        val bytes = Base64.decode(string)
        return SeedEngineMessageSignature(bytes)
    }
}
