data class Point(val pos: Vec2d, val vel: Vec2d) {
  fun tick(): Point = Point(pos + vel, vel)

  companion object {
    fun toPoint(s: String): Point {
      val vec2dRegex = """<([- ]\d+, [- ]\d+)>""".toRegex()
      val matches = vec2dRegex.findAll(s)
      val (pos) = matches.first().destructured
      val (vel) = matches.last().destructured
      return Point(Vec2d.parse(pos), Vec2d.parse(vel))
    }
  }
}

fun main() {
  fun printPoints(positions: List<Vec2d>) {
    val pairs = positions.map { it.x to it.y }.toSet()
    (positions.minOf { it.x }..positions.maxOf { it.x }).map {
      x -> (positions.maxOf { it.y } downTo positions.minOf { it.y }).map {
        y-> if (x to y in pairs) print("#") else print(".")
      }
      println("")
    }
  }

  fun parts1And2(points: List<Point>) {
    var currPoints = points
    (0 until 100_000).forEach { i ->
      val allPos = currPoints.map { it.pos }
      if (allPos.minBy { it.x }.distance(allPos.maxBy { it.x }) < 75 &&
          allPos.minBy { it.y }.distance(allPos.maxBy { it.y }) < 75) {
        println("Second " + i + ": ")
        printPoints(allPos)
        println("")
      }
      currPoints = currPoints.map { it.tick() }
    }
  }

  fun part2(points: List<Point>): Int {
    return points.size
  }

  val points = readInput("Day10").map { Point.toPoint(it) }
  parts1And2(points)
}
