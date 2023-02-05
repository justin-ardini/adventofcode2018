fun main() {
  val PLANT = 1
  val EMPTY = 0

  fun <T> List<T>.padded(n: Int, value: T): List<T> {
    return List(n) { value } + this + List(n) { value }
  }

  fun toPot(c: Char) = if (c == '#') PLANT else EMPTY

  fun toNotes(strs: List<String>): Map<List<Int>, Int> =
    strs.associate {
      val parts = it.split(" => ")
      parts[0].map { toPot(it) } to toPot(parts[1][0])
    }

  fun step(pots: List<Int>, notes: Map<List<Int>, Int>): List<Int> =
    pots.padded(3, EMPTY).windowed(size=5, step=1, partialWindows=false, { notes[it]!! })

  fun sumPots(pots: List<Int>, generation: Int): Int =
    pots.mapIndexed { i, p -> if (p == PLANT) i - generation else 0 }.sum()

  fun part1(startPots: List<Int>, notes: Map<List<Int>, Int>): Int {
    var pots = startPots
    (1..20).forEach {
      pots = step(pots, notes)
    }
    return sumPots(pots, 20)
  }

  fun part2(startPots: List<Int>, notes: Map<List<Int>, Int>): Long {
    var pots = startPots
    var s = sumPots(pots, 0)
    var diff = 0
    (1..100_000).forEach { i ->
      pots = step(pots, notes)
      var prevS = s
      s = sumPots(pots, i)
      var prevDiff = diff
      diff = s - prevS
      if (prevDiff == diff) {
        // Need three matching diffs, two gives a false positive.
        val nextS = sumPots(step(pots, notes), i + 1)
        if (nextS - s == diff) {
          return s + (50_000_000_000L - i) * diff
        }
      }
    }
    return -1
  }

  val input = readInput("Day12")
  val pots = input[0].split(": ")[1].map { toPot(it) }
  val notes = toNotes(input.drop(2))
  println(part1(pots, notes))
  println(part2(pots, notes))
}
