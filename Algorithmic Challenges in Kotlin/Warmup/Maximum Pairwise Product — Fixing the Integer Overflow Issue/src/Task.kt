import kotlin.math.max

fun maximumPairwiseProduct(a: IntArray): Long {
    val len = a.size
    var res = Int.MIN_VALUE.toLong()
    for (i in 0 until len) {
        for (j in i + 1 until len) {
            res = max(res, a[i].toLong() * a[j])
        }
    }
    return res
}