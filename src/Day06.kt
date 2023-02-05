fun main() {
  val TIE = -1

  fun closestPoint(p: Vec2d, points: List<Vec2d>): Pair<Vec2d, Int> {
    val topTwo = points.map { it to it.distance(p) }
        .sortedBy { it.second }
        .take(2)
    if (topTwo[0].second == topTwo[1].second) {
      return Vec2d(0, 0) to TIE
    }
    return topTwo[0]
  }

  fun isEdge(p: Vec2d, xRange: IntRange, yRange: IntRange): Boolean =
    p.x == xRange.first || p.x == xRange.last ||
        p.y == yRange.first || p.y == yRange.last

  fun part1(points: List<Vec2d>): Int {
    val xRange = points.minByOrNull { it.x }!!.x..points.maxByOrNull { it.x }!!.x
    val yRange = points.minByOrNull { it.y }!!.y..points.maxByOrNull { it.y }!!.y
    val edges = mutableSetOf<Vec2d>()
    return xRange.flatMap {
      x -> yRange.map {
        y ->
          val point = Vec2d(x, y)
          val (closest, distance) = closestPoint(point, points)
          if (isEdge(point, xRange, yRange)) {
            edges.add(closest)
          }
          closest.takeUnless { distance == TIE }
      }
    }
        .filterNot { it == null || it in edges }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }!!.value
  }

  fun totalDistance(p: Vec2d, points: List<Vec2d>): Int =
    points.map { it.distance(p) }.sum()

  fun part2(points: List<Vec2d>): Int {
    val xRange = points.minByOrNull { it.x }!!.x..points.maxByOrNull { it.x }!!.x
    val yRange = points.minByOrNull { it.y }!!.y..points.maxByOrNull { it.y }!!.y
    return xRange.flatMap {
      x -> yRange.map {
        y -> totalDistance(Vec2d(x, y), points)
      }
    }
        .filter { it < 10000 }
        .count()
  }

  val input = readInput("Day06").map { Vec2d.parse(it) }
  println(part1(input))
  println(part2(input))
}
