fun main() {
  val OPEN = '.'
  val TREE = '|'
  val YARD = '#'

  operator fun Array<CharArray>.get(v: Vec2d): Char =
    this[v.y][v.x]

  operator fun Array<CharArray>.set(v: Vec2d, to: Char) {
    this[v.y][v.x] = to
  }

  operator fun Array<CharArray>.contains(v: Vec2d): Boolean =
    v.x >= 0 && v.x < this[0].size && v.y >= 0 && v.y < this.size

  fun Array<CharArray>.count(c: Char): Int =
    this.sumOf { it.count { it == c } }

  fun Array<CharArray>.print() {
    this.forEach {
      println(it)
    }
  }

  val neighborOffsets = listOf(
    Vec2d(-1, -1),
    Vec2d(-1, 0),
    Vec2d(-1, 1),
    Vec2d(0, -1),
    Vec2d(0, 1),
    Vec2d(1, -1),
    Vec2d(1, 0),
    Vec2d(1, 1))

  fun tick(grid: Array<CharArray>): Array<CharArray> =
     grid.mapIndexed { y, row ->
       row.mapIndexed { x, c ->
         val v = Vec2d(x, y)
         val neighbors = neighborOffsets.map { v + it }.filter { it in grid }
         when (c) {
           OPEN -> if (neighbors.count { grid[it] == TREE } >= 3) TREE else OPEN
           TREE -> if (neighbors.count { grid[it] == YARD } >= 3) YARD else TREE
           else -> if (neighbors.count { grid[it] == YARD } >= 1 && neighbors.count { grid[it] == TREE } >= 1) YARD else OPEN
         }
       }.toCharArray()
     }.toTypedArray()

  fun run(startGrid: Array<CharArray>, minutes: Int): Array<CharArray> {
    var grid = startGrid
    (1..minutes).forEach {
      grid = tick(grid)
    }
    return grid
  }

  fun hash(grid: Array<CharArray>): Int =
    grid.count(TREE) * grid.count(YARD)

  fun part1(grid: Array<CharArray>): Int {
    val endGrid = run(grid, 10)
    return hash(endGrid)
  }

  fun findCycle(startGrid: Array<CharArray>, limit: Int): Pair<Int, Int> {
    var grid = startGrid
    val hashes = mutableMapOf<Int, Int>()
    (1..limit).forEach { min ->
      grid = tick(grid)
      val h = hash(grid)
      if (h in hashes && hashes[hash(tick(grid))] == hashes[h]!! + 1) {
        return hashes[h]!! to min
      }
      hashes[h] = min
    }
    return -1 to -1
  }

  fun part2(grid: Array<CharArray>): Int {
    val (startCycle, endCycle) = findCycle(grid, 1_000_000)
    val startGrid = run(grid, startCycle)
    val offset = (1_000_000_000 - startCycle) % (endCycle - startCycle)
    val endGrid = run(startGrid, offset)
    return hash(endGrid)
  }

  val grid = readInput("Day18").map { it.toCharArray() }.toTypedArray()
  println(part1(grid))
  println(part2(grid))
}
