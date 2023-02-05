fun main() {
  fun recipes(n: Int): List<Int> {
    val scoreboard = mutableListOf(3, 7)
    var i = 0
    var j = 1
    while (scoreboard.size < n) {
      var next = scoreboard[i] + scoreboard[j]
      var parts = if (next >= 10) listOf(next / 10, next % 10) else listOf(next)
      if (n - scoreboard.size == 1 && parts.size == 2) {
        scoreboard.add(parts[0])
        break
      }
      scoreboard += parts
      i = (i + scoreboard[i] + 1).mod(scoreboard.size)
      j = (j + scoreboard[j] + 1).mod(scoreboard.size)
    }
    return scoreboard
  }

  fun part1(input: Int): String {
    return recipes(input + 10).takeLast(10).joinToString("")
  }

  fun endsWith(scoreboard: List<Int>, pattern: Int): Boolean {
    return scoreboard.takeLast(6).joinToString("").takeLast(6).toInt() == pattern
  }

  fun recipes2(pattern: Int): Int {
    val scoreboard = mutableListOf(3, 7)
    var i = 0
    var j = 1
    while (true) {
      var next = scoreboard[i] + scoreboard[j]
      var parts = if (next >= 10) listOf(next / 10, next % 10) else listOf(next)
      parts.forEach {
        scoreboard.add(it)
        if (endsWith(scoreboard, pattern)) {
          return scoreboard.size - 6
        }
      }
      i = (i + scoreboard[i] + 1).mod(scoreboard.size)
      j = (j + scoreboard[j] + 1).mod(scoreboard.size)
    }
    return -1
  }

  fun part2(input: Int): Int {
    return recipes2(input)
  }

  val input = readInput("Day14")[0].toInt()
  println(part1(input))
  println(part2(input))
}
