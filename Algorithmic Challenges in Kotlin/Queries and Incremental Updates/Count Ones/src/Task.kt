
fun countOnes(seq: CharSequence): Int {
    var counter = 0
    for(bit in seq) if (bit == '1') ++counter
    return counter
}
