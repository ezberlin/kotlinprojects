import kotlinx.serialization.Serializable

@Serializable
data class ResourceType(
    val resourceTypeName: String,
    val subject: Subject,
    val inLMF: Boolean = false,
    )
