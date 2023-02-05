private enum class Tool {
  TORCH,
  CLIMBING_GEAR,
  NEITHER,
}

fun main() {
  val ORIGIN = Vec2d(0, 0)

  operator fun Array<IntArray>.get(v: Vec2d): Int =
    this[v.y][v.x]

  operator fun Array<IntArray>.set(v: Vec2d, to: Int) {
    this[v.y][v.x] = to
  }

  operator fun Array<IntArray>.contains(v: Vec2d): Boolean =
    v.x >= 0 && v.x < this[0].size && v.y >= 0 && v.y < this.size

  class Cave(val depth: Int, val target: Vec2d, val buffer: Vec2d = Vec2d(0, 0)) {
    val grid: Array<IntArray>
    init {
      grid = (0..target.y + buffer.y).map {
          (0..target.x + buffer.x).map {
            0
          }.toIntArray()
        }.toTypedArray()
    }

    fun geologicIndex(region: Vec2d): Int =
      when {
        region == ORIGIN -> 0
        region == target -> 0
        region.y == 0 -> region.x * 16807
        region.x == 0 -> region.y * 48271
        else -> grid[region - Vec2d(1, 0)] * grid[region - Vec2d(0, 1)]
      }

    fun erosionLevel(region: Vec2d): Int =
      (geologicIndex(region) + depth) % 20183

    fun fillGrid() {
      (0..target.y + buffer.y).forEach { y ->
        (0..target.x + buffer.x).forEach { x ->
          val region = Vec2d(x, y)
          grid[region] = erosionLevel(region)
        }
      }
    }
  }

  fun parseLine(line: String): Int =
    line.split(": ")[1].toInt()

  fun part1(depth: Int, target: Vec2d): Int {
    val cave = Cave(depth, target)
    cave.fillGrid()
    return cave.grid.sumOf { row ->
      row.sumOf { it % 3 }
    }
  }

  val validTools = listOf(
    setOf(Tool.CLIMBING_GEAR, Tool.TORCH),
    setOf(Tool.CLIMBING_GEAR, Tool.NEITHER),
    setOf(Tool.TORCH, Tool.NEITHER))

  fun shortestPath(grid: Array<IntArray>, start: Vec2d, target: Vec2d): Int {
    val q = ArrayDeque<Triple<Vec2d, Tool, Int>>()
    q.addLast(Triple(start, Tool.TORCH, 0))
    val memo = mutableMapOf<Pair<Vec2d, Tool>, Int>()
    var best = 1_000_000
    while (!q.isEmpty()) {
      var (pos, tool, mins) = q.removeFirst()
      if (pos == target) {
        if (tool != Tool.TORCH) {
          mins += 7
        }
        best = Math.min(best, mins)
        continue
      }
      var prevBest = memo.getOrDefault(pos to tool, 1_000_000)
      if (mins >= prevBest) {
        continue
      }
      memo[pos to tool] = mins
      // Move if possible.
      for (dir in listOf(Vec2d(1, 0), Vec2d(0, 1), Vec2d(-1, 0), Vec2d(0, -1))) {
        var nextPos = pos + dir
        if (nextPos in grid && tool in validTools[grid[nextPos]]) {
          q.addLast(Triple(nextPos, tool, mins + 1))
        }
      }
      // Switch to other valid tool.
      q.addLast(Triple(pos, validTools[grid[pos]].first { it != tool }, mins + 7))
    }
    return best
  }

  fun part2(depth: Int, target: Vec2d): Int {
    // Buffer to allow travel past target.
    val cave = Cave(depth, target, Vec2d(50, 50))
    cave.fillGrid()
    val grid = cave.grid.map { row ->
      row.map { it % 3 }.toIntArray()
    }.toTypedArray()
    return shortestPath(grid, Vec2d(0, 0), target)
  }

  val lines = readInput("Day22")
  val depth = lines[0].split(": ")[1].toInt()
  val target = Vec2d.parse(lines[1].split(": ")[1])
  println(part1(depth, target))
  println(part2(depth, target))
}
