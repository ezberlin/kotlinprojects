package com.example

import Priority
import Task
import com.example.model.TaskRepository

class FakeTaskRepository : TaskRepository {
    private val tasks = mutableListOf(
        Task("cleaning", "Clean the house", Priority.Low),
        Task("gardening", "Mow the lawn", Priority.Medium),
        Task("shopping", "Buy the groceries", Priority.High),
        Task("cleaning", "Clean the house", Priority.Low),


    )
}