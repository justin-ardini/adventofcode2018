fun main() {
  fun parseInput(input: String): Pair<Int, Int> {
    val parts = input.split(";")
    return parts[0].split(" ")[0].toInt() to parts[1].split(" ")[5].toInt()
  }

  fun shiftBy(deque: ArrayDeque<Int>, n: Int) {
    if (n < 0) {
      repeat(Math.abs(n)) {
        deque.addLast(deque.removeFirst())
      }
    } else {
      repeat(n) {
        deque.addFirst(deque.removeLast())
      }
    }
  }

  fun play(numPlayers: Int, lastMarble: Int): Long {
    val scores = LongArray(numPlayers)
    val marbles = ArrayDeque<Int>()
    marbles.add(0)

    for (n in 1..lastMarble) {
      if (n % 23 == 0) {
        shiftBy(marbles, -7)
        scores[n % numPlayers] += n + marbles.removeFirst().toLong()
        shiftBy(marbles, 1)
      } else {
        shiftBy(marbles, 1)
        marbles.addFirst(n)
      }
    }
    return scores.maxByOrNull { it }!!
  }

  fun part1(input: Pair<Int, Int>): Long {
    return play(input.first, input.second)
  }

  fun part2(input: Pair<Int, Int>): Long {
    return play(input.first, 100 * input.second)
  }

  val input = parseInput(readInput("Day09").get(0))
  println(part1(input))
  println(part2(input))
}
