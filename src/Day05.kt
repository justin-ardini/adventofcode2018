fun main() {
  fun matchingPair(a: Char?, b: Char): Boolean {
    return a != null && a != b && a.lowercaseChar() == b.lowercaseChar() 
  }

  /** Returns new polymer created by reaction. */
  fun String.react(): String {
    return this.fold(
        mutableListOf<Char>(),
        { acc, c ->
            if (matchingPair(acc.lastOrNull(), c)) acc.removeLast() else acc.add(c)
            acc })
        .joinToString(separator = "")
  }

  fun part1(input: String): Int {
    return input.react().length
  }

  fun part2(input: String): Int {
    return input.groupBy { it.lowercaseChar() }
       .mapValues {
           (_, l) -> input.filterNot { l.contains(it) }.react().length }
       .minByOrNull { it.value }!!.value
  }

  val input = readInput("Day05").get(0)
  println(part1(input))
  println(part2(input))
}
