package org.example

class Space {
    var content = " "
    var value = 0
        set(value) {
            if (value in 0..2) {
                field = value
                content = if (value == 1) "X" else if (value == 2) "O" else " "
            }
        }
}