import kotlinx.serialization.Serializable

@Serializable
data class Student (
    val studentName : String?,
    val studentFirstName: String?,
    val course: Course,
    val inLMF: Boolean,
    )