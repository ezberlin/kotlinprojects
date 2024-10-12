import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class Resource(
    val indexNumber: Int,
    @Contextual val dueDate: LocalDate? = null,

    var resourceType: ResourceType,
    var owner: Student?
)