package org.example

fun szudzik(x: Int, y: Int) = if (x >= y) x * x + x + y else y * y + x

object PointAmount {
    var number = 0
}

class Point {
    init {
        PointAmount.number++
    }
}

class Line(val p1: Point, val p2: Point) {
    override fun equals(other: Any?) =
        if (other !is Line) false
        else (p1 == other.p1 && p2 == other.p2) || (p1 == other.p2 && p2 == other.p1)

    override fun hashCode(): Int {
        var result = p1.hashCode()
        result = 31 * result + p2.hashCode()
        return result
    }
}

class Circle(val c: Point, val r: Point)

fun intersectLineLine(l1: Line, l2: Line): Point = 


fun main() {
    val p1 = Point()
    val p2 = Point()
}