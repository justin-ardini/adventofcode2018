fun main() {
  fun String.hasPairsOrTriples(): Pair<Boolean, Boolean> {
    val counts = this.groupingBy { it }.eachCount()
    return Pair(counts.any { it.value == 2 }, counts.any { it.value == 3 })
  }

  fun part1(input: List<String>): Int {
    val allPairs = input.map { it.hasPairsOrTriples() }
    return allPairs.count { it.first } * allPairs.count { it.second }
  }

  fun String.commonLetters(str: String): List<Char> {
    return this.zip(str).filter { (a, b) -> a == b }.map { it.first }
  }

  fun part2(input: List<String>): String {
    val targetSize = input.get(0).length - 1
    return input.asSequence().mapIndexed {
        i, strA -> input.asSequence().drop(i)
            .map { strB -> strA.commonLetters(strB).joinToString("") }
            .find { it.length == targetSize }
        }
        .filterNotNull()
        .first()
  }

  val input = readInput("Day02")
  println(part1(input))
  println(part2(input))
}
