import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val resources = mutableListOf<Resource>()

enum class AppWindows(val displayName: String) {
    Lending("Ausleihe"),
    Return("Rückgabe"),
    Students("Schüler"),
    Resources("Ressourcen"),
    ResourceTypes("Ressourcenarten"),
    Warnings("Mahnungen"),
}

fun main() = application {
    val windowFocusRequestSharedFlow = remember { MutableSharedFlow<AppWindows>() }
    var currentWindowType by remember { mutableStateOf(AppWindows.Resources) }

    Window(
        title = currentWindowType.displayName,
        onCloseRequest = ::exitApplication,
    ) {
        LaunchedEffect(Unit) {
            windowFocusRequestSharedFlow
                .filter { it == currentWindowType }
                .collect {
                    window.toFront()
                }
        }
        val scope = rememberCoroutineScope()
        Column {
            Row {
                for (appWindow in AppWindows.entries) {
                    Button({
                        scope.launch {
                            currentWindowType = appWindow
                            windowFocusRequestSharedFlow.emit(appWindow)
                        }
                    }) {
                        Text(appWindow.displayName)
                    }
                }
            }

            when (currentWindowType) {
                AppWindows.Lending -> Lending()
                AppWindows.Return -> Return()
                AppWindows.Students -> Students()
                AppWindows.Resources -> Resources()
                AppWindows.ResourceTypes -> ResourceTypes()
                AppWindows.Warnings -> Warnings()
            }
        }
    }
}

@Composable
@Preview
fun Lending() {
    addStudentToSystem(Student("Loch", "Finn", Course.C7, false, mutableListOf()))
    addStudentToSystem(Student("Drucker", "Tobi", Course.C7, true, mutableListOf()))
    addStudentToSystem(Student("Tisch", "Maria", Course.C7, true, mutableListOf()))
    addStudentToSystem(Student("Regal", "Bobby", Course.C7, true, mutableListOf()))
    addStudentToSystem(Student("Computer", "Brian", Course.B7, true, mutableListOf()))
    val buch = ResourceType("uwubuch", Subject.FRENCH, true, mutableListOf())
    addResourceTypeToSystem(buch)
    addResourceToResourceType(Resource(123456, LocalDate.now(), buch, null), buch)
    addResourceToResourceType(Resource(123457, LocalDate.now(), buch, null), buch)
    addResourceToResourceType(Resource(123458, LocalDate.now(), buch, null), buch)
    addResourceToResourceType(Resource(123459, LocalDate.now(), buch, null), buch)
    addResourceToResourceType(Resource(123450, LocalDate.now(), buch, null), buch)
    addResourceToResourceType(Resource(123451, LocalDate.now(), buch, null), buch)



    MaterialTheme {
        var course by remember { mutableStateOf<Course?>(null) }
        var resourceType by remember { mutableStateOf<ResourceType?>(null) }
        val currentlyLending by derivedStateOf { course != null && resourceType != null }
        val lendingList by derivedStateOf { students
            .filter { it.course == course }
            .sortedBy { it.studentName }
            .filter { resourceType?.inLMF == it.inLMF } }
        var listIndex by remember { mutableStateOf(0) }
        Column {
            Row {
                EnumDropdownMenu(
                    enumValues = Course.entries,
                    selectedOption = course,
                    onOptionSelected = { option ->
                        course = option
                    },
                    displayName = { it.displayName }
                )
                ResourceTypeDropdownMenu(
                    selectedOption = resourceType,
                    onOptionSelected = { option ->
                        resourceType = option
                    }
                )
            }
            if (currentlyLending) {
                TableScreen(listOf(listOf()), listOf("${lendingList[listIndex].studentName}, " +
                        "${lendingList[listIndex].studentFirstName} " +
                        "(${lendingList[listIndex].course.displayName})"))
                TableScreen(listOf(lendingList[listIndex].resources.map { it.resourceType.subject.displayName },
                    lendingList[listIndex].resources.map { it.resourceType.resourceTypeName },
                    lendingList[listIndex].resources.map { it.indexNumber },
                    lendingList[listIndex].resources.map { it.dueDate?.format(DateTimeFormatter.ofPattern("d.M.Y")) ?: "" }
                    ), listOf("Fach", "Name", "Index", "Fällig"))
            }
        }

    }
}

@Composable
@Preview
fun Return() {
    MaterialTheme {
    }
}

@Composable
@Preview
fun Students() {
    MaterialTheme {
        TableScreen(
            listOf(
                students.map { if (!it.studentName.isNullOrEmpty() && !it.studentFirstName.isNullOrEmpty()) "${it.studentName}, ${it.studentFirstName}" else "Nicht ausgeliehen"},
                students.map { it.course.displayName },
                students.map { if (it.resources.isNotEmpty()) it.resources[0].resourceType.resourceTypeName else ""}
            ), listOf("Name", "Klasse", "Erstes Ausleihwerk")
        )
    }
}

@Composable
@Preview
fun Resources() {
    MaterialTheme {
        TableScreen(
            listOf(
                resources.map { it.resourceType.resourceTypeName },
                resources.map { it.indexNumber },
                resources.map { it.dueDate?.format(DateTimeFormatter.ofPattern("d.M.Y")) ?: "" }
            ), listOf("Name", "Index", "Fälligkeitsdatum")
        )
    }
}

@Composable
@Preview
fun ResourceTypes() {
    MaterialTheme {
        TableScreen(
            listOf(
                resourceTypes.map { it.resourceTypeName },
                resourceTypes.map { it.subject.displayName },
                resourceTypes.map { if (it.inLMF) "Ja" else "Nein" }
            ), listOf("Name", "Fach", "Im Lernmittelfonds")
        )
    }
}

@Composable
@Preview
fun Warnings() {
    MaterialTheme {
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
@Preview
fun TableScreen(table: List<List<Any>>, headers: List<String>) {
    val transposedTable = table[0].indices.map { rowIndex ->
        table.map { it[rowIndex] }
    }

    val columnWeight = 1f / headers.size
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(Modifier.background(Color.Gray)) {
                for (header in headers) {
                    TableCell(text = header, weight = columnWeight)
                }
            }
        }
        items(transposedTable) { row ->
            Row(Modifier.fillMaxWidth()) {
                for (element in row) {
                    TableCell(text = element.toString(), weight = columnWeight)
                }
            }
        }
    }
}

@Composable
@Preview
fun <T> EnumDropdownMenu(
    enumValues: List<T>,
    selectedOption: T?,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    displayName: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    Row {
        Box(
            modifier = modifier
                .padding(16.dp)
                .background(Color.LightGray)
                .clickable { expanded = true }
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                text = selectedOption?.let { displayName(it) } ?: "Klasse",
                modifier = Modifier.padding(16.dp),
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            enumValues.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = displayName(option))
                }
            }
        }
    }

}

@Composable
@Preview
fun ResourceTypeDropdownMenu(
    selectedOption: ResourceType?,
    onOptionSelected: (ResourceType) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Row {
        Box(
            modifier = modifier
                .padding(16.dp)
                .background(Color.LightGray)
                .clickable { expanded = true }
                .clip(RoundedCornerShape(8.dp))
        ) {
            Text(
                text = selectedOption?.resourceTypeName ?: "Ressource",
                modifier = Modifier.padding(16.dp),
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            resourceTypes.forEach { option ->
                DropdownMenuItem(onClick = {
                    onOptionSelected(option)
                    expanded = false
                }) {
                    Text(text = option.resourceTypeName)
                }
            }
        }
    }

}


fun removeResourceFromStudent(student: Student, resource: Resource) {
    students.map { if (it == student) it.resources.remove(resource) }
    resources.map { if (it == resource) it.owner = null }
}

fun addResourceToStudent(student: Student, resource: Resource) {
    students.map { if (it == student) it.resources.add(resource) }
    resources.map { if (it == resource) it.owner = student }
}

fun addStudentToSystem(student: Student) {
    students.add(student)
}

fun removeStudentFromSystem(student: Student) {
    students.removeIf { it == student }
    resources.map { if (it.owner == student) it.owner = null }
}

fun addResourceTypeToSystem(resourceType: ResourceType) {
    resourceTypes.add(resourceType)
}

fun removeResourceTypeFromSystem(resourceType: ResourceType) {
    resourceTypes.removeIf { it == resourceType }
    resources.removeIf { it.resourceType == resourceType }
}

fun addResourceToResourceType(resource: Resource, resourceType: ResourceType) {
    resourceTypes.map { if (it == resourceType) it.resources.add(resource) }
    resource.resourceType = resourceType
    resources.add(resource)
}

fun removeResourceFromSystem(resource: Resource) {
    students.forEach {
        student -> student.resources.removeIf {
            it == resource
        }
    }
    resourceTypes.forEach {
        resourceType -> resourceType.resources.removeIf {
            it == resource
        }
    }
    resources.removeIf { it == resource }
}
