
class CountOnesWithUpdates(private var seq: CharSequence)  {
    var counter = for(bit in seq) if (bit == '1') ++counter

    fun countOnes() : Int {
        var counter = 0
        for(bit in seq) if (bit == '1') ++counter
        return counter
    }

    fun flip(index: Int) {

    }
}
