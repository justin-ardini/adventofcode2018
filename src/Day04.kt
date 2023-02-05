fun main() {
  fun parseInput(input: List<String>): Map<Int, List<Int>> {
    val idRegex = """^.+ #(\d+) .+$""".toRegex()
    val minuteRegex = """^\[.+ \d\d:(\d\d)] .+$""".toRegex()
    val sleepsById = mutableMapOf<Int, MutableList<Int>>()
    var currId: Int = -1;
    var sleepStart: Int = -1;
    input.sorted().forEach { 
      line -> when {
        line.contains("#") -> {
          currId = idRegex.find(line)!!.destructured.component1().toInt()
        }
        line.contains("falls") -> {
          sleepStart = minuteRegex.find(line)!!.destructured.component1().toInt()
        }
        line.contains("wakes") -> {
          val sleepEnd = minuteRegex.find(line)!!.destructured.component1().toInt()
          sleepsById.getOrPut(currId) { mutableListOf() }
              .addAll((sleepStart until sleepEnd).toList())
        }
        else -> throw IllegalArgumentException("Invalid line")
      }
    }
    return sleepsById
  }

  fun part1(sleepsById: Map<Int, List<Int>>): Int {
    val id = sleepsById.maxByOrNull { it.value.size }!!.key
    val minute = sleepsById.get(id)!!.groupBy { it }
        .maxByOrNull { it.value.size }!!.key
    return id * minute
  }

  fun part2(sleepsById: Map<Int, List<Int>>): Int {
    return sleepsById.mapValues {
      // List<Int> -> Pair<Int, Int>
      (_, v) -> v.groupBy { it }
          .mapValues { it.value.size }
          .maxByOrNull { it.value }!!
          .toPair()
    }
        .maxByOrNull { it.value.second }!!
        .run { key * value.first }
  }

  val input = parseInput(readInput("Day04"))
  println(part1(input))
  println(part2(input))
}
