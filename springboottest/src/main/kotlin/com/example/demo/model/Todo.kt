package com.example.demo.model

data class Todo(
    val id: Long? = null,
    var title: String,
    var completed: Boolean = false
)
