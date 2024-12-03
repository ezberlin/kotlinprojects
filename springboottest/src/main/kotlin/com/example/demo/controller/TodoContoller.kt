package com.example.demo.controller

import com.example.demo.model.Todo
import com.example.demo.repository.TodoRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(private val todoRepository: TodoRepository) {

    @GetMapping
    fun getAllTodos(): List<Todo> = todoRepository.findAll()

    @PostMapping
    fun createTodo(@RequestBody todo: Todo): ResponseEntity<Todo> {
        val savedTodo = todoRepository.save(todo)
        return ResponseEntity(savedTodo, HttpStatus.CREATED)
    }

    @PutMapping("/{id}")
    fun updateTodo(@PathVariable id: Long, @RequestBody todo: Todo): ResponseEntity<Todo> {
        return if (id == todo.id) {
            todoRepository.update(todo)
            ResponseEntity(todo, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<Void> {
        todoRepository.deleteById(id)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
