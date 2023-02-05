import java.util.PriorityQueue

fun main() {

  fun findPath(nextNodes: Map<String, List<String>>, prevNodes: Map<String, List<String>>): String {
    val startNodes = nextNodes.keys.filterNot { it in prevNodes.keys }
    val queue = PriorityQueue(startNodes)
    val visited = startNodes.toMutableSet()
    val path = mutableListOf<String>()
    while (!queue.isEmpty()) {
      val node = queue.poll()
      path.add(node)
      val next = nextNodes.getOrDefault(node, listOf<String>())
          .filterNot { it in visited }
          .filter {
            prevNodes.getOrDefault(it, listOf<String>()).all { it in path }
          }
      visited.addAll(next)
      queue.addAll(next)
    }
    return path.joinToString(separator = "")
  }

  fun part1(input: List<Pair<String, String>>): String {
    val nextNodes = input.groupBy({ it.first }, { it.second })
    val prevNodes = input.groupBy({ it.second }, { it.first })
    return findPath(nextNodes, prevNodes)
  }

  fun getCost(node: String) = 60 + node.first().code - 64

  fun minTimeWithWorkers(nextNodes: Map<String, List<String>>,
      prevNodes: Map<String, List<String>>, workers: Int): Int {
    val startNodes = nextNodes.keys.filterNot { it in prevNodes.keys }
    val numNodes = (nextNodes.keys + prevNodes.keys).size
    val queue = PriorityQueue(startNodes)
    val visited = startNodes.toMutableSet()
    val path = mutableListOf<String>()
    var availableTimes = buildList {
      for (i in 0 until workers) {
        add(0 to "")
      }
    }
    for (second in 0..10000) {
      // Finish actions
      availableTimes.filter { it.first == second && it.second != "" }
        .forEach { (_, node) ->
          path.add(node)
          val next = nextNodes.getOrDefault(node, listOf<String>())
              .filterNot { it in visited }
              .filter {
                prevNodes.getOrDefault(it, listOf<String>()).all { it in path }
              }
          visited.addAll(next)
          queue.addAll(next)
        }
      if (path.size == numNodes) {
        return second
      }
      // Start new actions
      availableTimes = availableTimes.map {
        (s, prev) ->
          if (s > second || queue.isEmpty()) {
            return@map s to prev
          }
          val node = queue.poll()
          second + getCost(node) to node
        }
    }
    return -1
  }

  fun part2(input: List<Pair<String, String>>): Int {
    val nextNodes = input.groupBy({ it.first }, { it.second })
    val prevNodes = input.groupBy({ it.second }, { it.first })
    return minTimeWithWorkers(nextNodes, prevNodes, 5)
  }

  val input = readInput("Day07").map {
    val parts = it.split(" ")
    parts[1] to parts[7]
  }
  println(part1(input))
  println(part2(input))
}
