package main

fun main() {
    print("Gib eine Matrix ein: ")
    val matrix1 = stringZuMatrix(readlnOrNull()!!)
    matrix1?.let { printeMatrix(it) } ?: run {
        println("Fehler: Die eingegebene Matrix ist ungültig.")
        return
    }

    print("Gib eine andere Matrix ein: ")
    val matrix2 = stringZuMatrix(readlnOrNull()!!)
    matrix2?.let { printeMatrix(it) } ?: run {
        println("Fehler: Die eingegebene Matrix ist ungültig.")
        return
    }

    print("Willst du sie multiplizieren (m) oder addieren (a)? ")
    if (readlnOrNull() == "m") {
        multipliziere(matrix1, matrix2)
            ?.let { printeMatrix(it) }
            ?: println("Die Spaltenzahl der ersten Matrix muss der Reihenzahl der zweiten entsprechen!")
    } else {
        addiere(matrix1, matrix2)
            ?.let { printeMatrix(it) }
            ?: println("Beide Matrixen müssen die gleiche Reihen- und Spaltenzahl haben!")
    }
}

fun addiere(matrixa: Matrix, matrixb: Matrix): Matrix? {
    if((matrixa.m != matrixb.m) || (matrixa.n != matrixb.n)) return null
    for (i in matrixa.matrix.indices) for (j in matrixa.matrix[i].indices) {
        matrixa.matrix[i][j] += matrixb.matrix[i][j]
    }
    return matrixa
}

fun multipliziere(matrixa : Matrix, matrixb : Matrix): Matrix? {
    if(matrixa.n != matrixb.m) return null
    val matrixc = erstelleMatrix(matrixa.m, matrixb.n)
    for(i in 0..<matrixa.m) for(j in 0..< matrixb.n) for(k in 0..<matrixa.n) {
        matrixc.matrix[i][j] += matrixa.matrix[i][k] * matrixb.matrix[k][j]
    }
    return matrixc
}

fun printeMatrix(matrix : Matrix) {
    for(reihe in matrix.matrix) println(reihe.contentToString())
}

fun erstelleMatrix(m: Int = 0, n: Int = 0) : Matrix{
    return Matrix(Array(m){DoubleArray(n)})
}

fun stringZuMatrix(string: String): Matrix? {
    val geteilterString = string.split("#")
    val l = mutableListOf<MutableList<Double>>()

    try {
        for (substring in geteilterString) {
            val subl = mutableListOf<Double>()
            for (num in substring.split(" ")) {
                subl.add(num.toDouble())
            }
            l.add(subl)
        }
    } catch (e: NumberFormatException) {
        return null
    }

    val matrixArray = Array(l.size) { i -> l[i].toDoubleArray() }
    return Matrix(matrixArray)
}