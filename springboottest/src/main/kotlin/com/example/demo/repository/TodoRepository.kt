package com.example.demo.repository

import com.example.demo.model.Todo
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.KeyHolder
import org.springframework.stereotype.Repository
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Statement

@Repository
class TodoRepository(private val jdbcTemplate: JdbcTemplate) {

    init {
        // Create the table if it doesn't exist
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS todos (id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(255), completed BOOLEAN)")
    }

    fun findAll(): List<Todo> {
        return jdbcTemplate.query("SELECT * FROM todos") { rs, _ ->
            Todo(
                id = rs.getLong("id"),
                title = rs.getString("title"),
                completed = rs.getBoolean("completed")
            )
        }
    }

    fun save(todo: Todo): Todo {
        val sql = "INSERT INTO todos (title, completed) VALUES (?, ?)"
        val keyHolder: KeyHolder = GeneratedKeyHolder()

        jdbcTemplate.update({ connection: Connection ->
            val ps: PreparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
            ps.setString(1, todo.title)
            ps.setBoolean(2, todo.completed)
            ps
        }, keyHolder)

        return todo.copy(id = keyHolder.key?.toLong())
    }

    fun update(todo: Todo) {
        jdbcTemplate.update("UPDATE todos SET title = ?, completed = ? WHERE id = ?", todo.title, todo.completed, todo.id)
    }

    fun deleteById(id: Long) {
        jdbcTemplate.update("DELETE FROM todos WHERE id = ?", id)
    }
}
