data class Node(val children: List<Node>, val metadata: List<Int>) {
  fun sumP1(): Int =
    metadata.sum() + children.sumOf { it.sumP1() }

  fun sumP2(): Int =
    if (children.isEmpty()) metadata.sum()
    else metadata.sumOf { children.getOrNull(it-1)?.sumP2() ?: 0 }

  companion object {
    private fun toNodePair(nums: List<Int>): Pair<Node, Int> {
      val numChildren = nums[0]
      val numMetadata = nums[1]

      val children = mutableListOf<Node>()
      var next = 2
      for (i in 0 until numChildren) {
        val childAndNext = Node.toNodePair(nums.drop(next))
        children.add(childAndNext.first)
        next += childAndNext.second
      }

      val metadata = nums.slice(next until next + numMetadata)
      return Node(children, metadata) to next + numMetadata
    }

    fun toNode(nums: List<Int>): Node {
      return Node.toNodePair(nums).first
    }
  }
}

fun main() {
  fun part1(root: Node): Int {
    return root.sumP1()
  }

  fun part2(root: Node): Int {
    return root.sumP2()
  }

  val input = readInput("Day08").get(0).split(" ").map { it.toInt() }
  val root = Node.toNode(input)
  println(part1(root))
  println(part2(root))
}
