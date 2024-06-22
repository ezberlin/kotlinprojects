fun maximumPairwiseProduct(a: IntArray): Long {
    a.sortDescending()
    return a[0] * a[1].toLong()
}
