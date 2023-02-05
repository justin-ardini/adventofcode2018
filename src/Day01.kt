fun main() {
  fun <T> List<T>.repeatedSequence(): Sequence<T> = sequence {
    while (true) {
      yieldAll(this@repeatedSequence)
    }
  }

  fun part1(input: List<Int>): Int {
    return input.sum()
  }

  fun part2(input: List<Int>): Int {
    val frequencies = mutableSetOf(0)
    var sum = 0
    return input.repeatedSequence().map {
      sum += it
      sum
    }.first { !frequencies.add(it) }
  }

  val input = readInput("Day01").map { it.toInt() }
  println(part1(input))
  println(part2(input))
}
