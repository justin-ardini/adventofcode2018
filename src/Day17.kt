fun main() {
  val DOWN = Vec2d(0, 1)
  val LEFT = Vec2d(-1, 0)
  val RIGHT = Vec2d(1, 0)
  val SPOUT = Vec2d(500, 0)

  fun parseLine(input: String): Pair<IntRange, IntRange> {
    val (first, second) = input.split(", ")
    val a = first.drop(2).toInt()
    val (start, end) = second.split("..")
    val b = start.drop(2).toInt()..end.toInt()
    if (first.startsWith('x'))
      return a..a to b
    else
      return b to a..a
  }

  /** If true, returns set of newly blocked tiles. */
  fun isAtRest(start: Vec2d, blocked: Set<Vec2d>): Pair<Boolean, Set<Vec2d>> {
    val checked = mutableSetOf(start)
    var pos = start
    while (pos + LEFT !in blocked) {
      pos = pos + LEFT
      if (pos + DOWN !in blocked) {
        return false to setOf()
      }
      checked.add(pos)
    }
    pos = start
    while (pos + RIGHT !in blocked) {
      pos = pos + RIGHT
      if (pos + DOWN !in blocked) {
        return false to setOf()
      }
      checked.add(pos)
    }
    return true to checked
  }

  fun fillWithWater(start: Vec2d, floor: Int, blocked: Set<Vec2d>, checked: MutableSet<Vec2d>): Set<Vec2d> {
    var pos = start
    checked.add(pos)
    if (pos.y > floor) {
      return blocked
    }
    while (pos + DOWN !in blocked) {
      pos = pos + DOWN
      checked.add(pos)
      if (pos.y > floor) {
        return blocked
      }
    }

    val (atRest, water) = isAtRest(pos, blocked)
    if (atRest) {
      return blocked + water
    }

    val middle = pos
    while (pos + DOWN in blocked && pos + LEFT !in blocked && pos + LEFT !in checked) {
      pos = pos + LEFT
      checked.add(pos)
    }
    var newBlocked = blocked
    if (pos + DOWN !in blocked) {
      newBlocked = fillWithWater(pos + DOWN, floor, blocked, checked)
    }

    pos = middle
    while (pos + DOWN in newBlocked && pos + RIGHT !in newBlocked && pos + RIGHT !in checked) {
      pos = pos + RIGHT
      checked.add(pos)
    }
    if (pos + DOWN !in newBlocked) {
      newBlocked = fillWithWater(pos + DOWN, floor, newBlocked, checked)
    }

    return newBlocked
  }

  fun part1(clay: Set<Vec2d>): Int {
    val (yStart, yEnd) = clay.minOf { it.y } to clay.maxOf { it.y }
    var blocked = setOf<Vec2d>()
    var nextBlocked = clay
    var checked = mutableSetOf<Vec2d>()
    while (blocked.size != nextBlocked.size) {
      blocked = nextBlocked
      checked = mutableSetOf<Vec2d>()
      nextBlocked = fillWithWater(SPOUT, yEnd, blocked, checked)
    }
    return (checked + blocked - clay).count { it.y >= yStart && it.y <= yEnd }
  }

  fun part2(clay: Set<Vec2d>): Int {
    val (yStart, yEnd) = clay.minOf { it.y } to clay.maxOf { it.y }
    var blocked = setOf<Vec2d>()
    var nextBlocked = clay
    var checked = mutableSetOf<Vec2d>()
    while (blocked.size != nextBlocked.size) {
      blocked = nextBlocked
      checked = mutableSetOf<Vec2d>()
      nextBlocked = fillWithWater(SPOUT, yEnd, blocked, checked)
    }
    return (blocked - clay).count { it.y >= yStart && it.y <= yEnd }
  }

  val pairs = readInput("Day17").map { parseLine(it) }
  val clay = pairs.flatMap { (xr, yr) ->
    yr.flatMap { y ->
      xr.map { x -> Vec2d(x, y) }
    }
  }.toSet()
  println(part1(clay))
  println(part2(clay))
}
